package bio.terra.command;

import bio.terra.datarepo.model.FileModel;
import bio.terra.datarepo.model.FileModelType;
import bio.terra.model.DRElement;
import bio.terra.model.DRFile;
import bio.terra.model.DRRoot;
import bio.terra.parser.Argument;
import bio.terra.parser.Command;
import bio.terra.parser.Option;
import bio.terra.parser.ParsedResult;
import bio.terra.parser.Syntax;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

// object types
// dir, file, dataset, snapshot, cont(ainer)
//
// hierarchy is:
//  /                  - contains dataset and snapshot objects
//  /<dataset>/        - contains pseudo-dirs 'files' and 'tables'
//  /<dataset>/files/  - the '/' directory of the file system
//  /<dataset>/tables/ - the tables in the dataset
//
// TODO: snapshot layer
// TODO: use TableFormatter

public final class DRCommands {
    private static final String LIST_FORMAT = "%s%-8s  %-20s  %s  %s  %s%n";

    private DRCommands() { }

    public static Syntax getSyntax() {
        return new Syntax()
                .addCommand(new Command()
                        .primaryNames(new String[]{"dr", "list"})
                        .alternateNames(new String[]{"ls"})
                        .commandId(CommandEnum.COMMAND_DR_LIST.getCommandId())
                        .help("list data repo objects")
                        .addOption(new Option()
                                .shortName("R")
                                .longName("recurse")
                                .hasArgument(false)
                                .optional(true)
                                .help("Recurses from the path listing all elements under the path"))
                        .addArgument(new Argument()
                                .name("path")
                                .optional(true)
                                .help("Path to an object - sorry, no wildcards yet")))
                .addCommand(new Command()
                        .primaryNames(new String[]{"dr", "tree"})
                        .alternateNames(new String[]{"tree"})
                        .commandId(CommandEnum.COMMAND_DR_TREE.getCommandId())
                        .help("tree formatted list of data repo objects")
                        .addOption(new Option()
                                .shortName("d")
                                .longName("depth")
                                .hasArgument(true)
                                .optional(true)
                                .help("depth to recurse; if unspecified, the full tree is traversed"))
                        .addArgument(new Argument()
                                .name("path")
                                .optional(true)
                                .help("Path to an object - sorry, no wildcards yet")))
                .addCommand(new Command()
                        .primaryNames(new String[]{"dr", "describe"})
                        .alternateNames(new String[]{"describe"})
                        .commandId(CommandEnum.COMMAND_DR_DESCRIBE.getCommandId())
                        .help("describe a data repo object")
                        .addArgument(new Argument()
                                .name("path")
                                .optional(true)
                                .help("Path to an object - sorry, no wildcards yet"))
                        .addOption(new Option()
                                .longName("format")
                                .hasArgument(true)
                                .optional(true)
                                .help("Choose format; 'text' is the default; 'json' is supported")))
                .addCommand(new Command()
                        .primaryNames(new String[]{"dr", "stream"})
                        .alternateNames(new String[]{"cat"})
                        .commandId(CommandEnum.COMMAND_DR_STREAM.getCommandId())
                        .help("stream an object to standard out")
                        .addArgument(new Argument()
                                .name("path")
                                .optional(false)
                                .help("Path to an object")));
    }

    public static boolean dispatchCommand(CommandEnum command, ParsedResult result) {
        switch (command) {
            case COMMAND_DR_LIST:
                DRCommands.drList(result.getArgument("path"), result.found("recurse"));
                break;
            case COMMAND_DR_TREE:
                int depth = (result.found("depth")) ? Integer.parseInt(result.getArgument("depth")) : 1000000000;
                DRCommands.drTree(result.getArgument("path"), depth);
                break;
            case COMMAND_DR_DESCRIBE:
                DRCommands.drDescribe(result.getArgument("path"), result.getArgument("format"));
                break;
            case COMMAND_DR_STREAM:
                DRCommands.drStream(result.getArgument("path"));
                break;
            default:
                return false;
        }

        return true;
    }

    private static void drDescribe(String inPath, String format) {
        format = CommandUtils.validateFormat(format);
        DRElement element = lookup(inPath);
        element.describe(format);
    }

    private static void drList(String inPath, boolean recurse) {
        String path = CommandUtils.makeFullPath(inPath);

        DRElement elementToList = lookup(path);

        if (recurse) {
            listRecursive(elementToList, path);
        } else {
            List<DRElement> elementList = elementToList.enumerate();
            printElementList(elementList, 0);
        }
    }

    private static void listRecursive(DRElement element, String path) {
        System.out.println(path + ":");
        List<DRElement> elementList = element.enumerate();
        printElementList(elementList, 0);
        System.out.println("");

        for (DRElement enumElement : elementList) {
            if (!enumElement.isLeaf()) {
                listRecursive(enumElement, path + CommandUtils.SLASH + enumElement.getObjectName());
            }
        }
    }

    private static void drTree(String inPath, int maxDepth) {
        DRElement elementToList = lookup(inPath);
        treeRecursive(elementToList, maxDepth, 0);
    }

    private static void treeRecursive(DRElement element, int maxDepth, int currentDepth) {
        treePrint(element, currentDepth);
        if (currentDepth < maxDepth && !element.isLeaf()) {
            List<DRElement> elementList = element.enumerate();
            for (DRElement enumElement : elementList) {
                treeRecursive(enumElement, maxDepth, currentDepth + 1);
            }
        }
    }

    private static void treePrint(DRElement element, int currentDepth) {
        String prefix = StringUtils.repeat("|   ", currentDepth);
        System.out.printf("%s%s (%s)%n",
                prefix,
                element.getObjectName(),
                element.getObjectType().getName());
    }

    private static void printElementList(List<DRElement> elementList, int indent) {
        for (DRElement element : elementList) {
            printElement(element, indent);
        }
    }

    private static void printElement(DRElement element, int indent) {
        String indentString = StringUtils.repeat(' ', indent);
        System.out.printf(LIST_FORMAT,
                indentString,
                element.getObjectType().getName(),
                element.getObjectName(),
                element.getCreated(),
                element.getId(),
                element.getDescription());
    }

    private static void drStream(String inPath) {
        DRElement element = lookup(inPath);
        if (element instanceof DRFile) {
            DRFile file = (DRFile) element;
            FileModel fileModel = file.getFileModel();
            if (fileModel.getFileType() == FileModelType.FILE) {
                StreamFile.streamFile(fileModel.getFileDetail().getAccessUrl());
            }
        }
        CommandUtils.printErrorAndExit("You can only stream files right now");
    }

    // General element lookup
    private static DRElement lookup(String inPath) {
        String path = CommandUtils.makeFullPath(inPath);
        String[] pathParts = StringUtils.split(path, CommandUtils.SLASH);
        LinkedList<String> pathList = new LinkedList<>(Arrays.asList(pathParts));
        return new DRRoot().lookup(pathList);
    }



}

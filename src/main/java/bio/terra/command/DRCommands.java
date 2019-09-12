package bio.terra.command;

import bio.terra.datarepo.model.FileModel;
import bio.terra.datarepo.model.FileModelType;
import bio.terra.model.DRElement;
import bio.terra.model.DRFile;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

// object types
// dir, file, dataset, snapshot, cont(ainer)
//
// hierarchy is:
//  /                - contains dataset and snapshot objects
//  /<dataset>/        - contains pseudo-dirs 'files' and 'tables'
//  /<dataset>/files/  - the '/' directory of the file system
//  /<dataset>/tables/ - the tables in the dataset
//
// TODO: snapshot layer

public class DRCommands {
    private static final String LIST_FORMAT = "%s%-8s  %-20s  %s  %s  %s\n";

    private static DRCommands theDRCommands;

    private DRCommands() {
    }

    public static DRCommands getInstance() {
        if (theDRCommands == null) {
            theDRCommands = new DRCommands();
        }
        return theDRCommands;
    }

    public void drDescribe(String inPath) {
        DRElement element = DRLookup.getInstance().lookup(inPath);
        element.describe();
    }

    public void drList(String inPath, boolean recurse) {
        String path = CommandUtils.makeFullPath(inPath);

        DRElement elementToList = DRLookup.getInstance().lookup(path);

        if (recurse) {
            listRecursive(elementToList, path);
        } else {
            List<DRElement> elementList = elementToList.enumerate();
            printElementList(elementList, 0);
        }
    }

    private void listRecursive(DRElement element, String path) {
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

    public void drTree(String inPath, int maxDepth) {
        DRElement elementToList = DRLookup.getInstance().lookup(inPath);
        treeRecursive(elementToList, maxDepth, 0);
    }

    private void treeRecursive(DRElement element, int maxDepth, int currentDepth) {
        treePrint(element, currentDepth);
        if (currentDepth < maxDepth && !element.isLeaf()) {
            List<DRElement> elementList = element.enumerate();
            for (DRElement enumElement : elementList) {
                treeRecursive(enumElement, maxDepth, currentDepth + 1);
            }
        }
    }

    private void treePrint(DRElement element, int currentDepth) {
        String prefix = StringUtils.repeat("|   ", currentDepth);
        System.out.printf("%s%s (%s)\n",
                prefix,
                element.getObjectName(),
                element.getObjectType().getName());
    }


    private void printElementList(List<DRElement> elementList, int indent) {
        for (DRElement element : elementList) {
            printElement(element, indent);
        }
    }

    private void printElement(DRElement element, int indent) {
        String indentString = StringUtils.repeat(' ', indent);
        System.out.printf(LIST_FORMAT,
                indentString,
                element.getObjectType().getName(),
                element.getObjectName(),
                element.getCreated(),
                element.getId(),
                element.getDescription());
    }

    public void drStream(String inPath) {
        DRElement element = DRLookup.getInstance().lookup(inPath);
        if (element instanceof DRFile) {
            DRFile file = (DRFile) element;
            FileModel fileModel = file.getFileModel();
            if (fileModel.getFileType() == FileModelType.FILE) {
                StreamFile.streamFile(fileModel.getFileDetail().getAccessUrl());
            }
        }
        CommandUtils.printErrorAndExit("You can only stream files right now");
    }

}

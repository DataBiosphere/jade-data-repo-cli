package bio.terra;

import bio.terra.command.CommandEnum;
import bio.terra.command.CommandUtils;
import bio.terra.command.DRCommands;
import bio.terra.command.HelpCommands;
import bio.terra.command.DatasetCommands;
import bio.terra.command.ProfileCommands;
import bio.terra.context.Context;
import bio.terra.context.ContextEnum;
import bio.terra.parser.Argument;
import bio.terra.parser.Command;
import bio.terra.parser.Option;
import bio.terra.parser.ParsedResult;
import bio.terra.parser.Parser;
import bio.terra.parser.Syntax;

public class Main {

    public static void main(String[] args) {

        try {
            Parser parser = new Parser(makeSyntax());
            ParsedResult result = parser.parse(args);
            if (result == null) {
                // If parser returns no result, then there was a parse error. The parser has written
                // the proper message to stderr.
                System.exit(1);
            }
            CommandEnum command = CommandEnum.commandIdToEnum(result.getCommandId());

            switch(command) {
                case COMMAND_HELP: {
                    HelpCommands cmds = new HelpCommands(parser);
                    cmds.helpCommand(result.getArgument("primary"), result.getArgument("secondary"));
                }
                break;

                case COMMAND_DATASET_CREATE:
                    DatasetCommands.getInstance().datasetCreate(result.getArgument("input-json"));
                    break;
                case COMMAND_DATASET_SHOW:
                    DatasetCommands.getInstance().datasetShow(result.getArgument("dataset-name"));
                    break;
                case COMMAND_DATASET_DELETE:
                    DatasetCommands.getInstance().datasetDelete(result.getArgument("dataset-name"));
                    break;
                case COMMAND_DATASET_FILE:
                    DatasetCommands.getInstance().datasetIngestFile(
                            result.getArgument("dataset-name"),
                            result.getArgument("profile-id"),
                            result.getArgument("input-gspath"),
                            result.getArgument("target-path"),
                            result.getArgument("mime-type"),
                            result.getArgument("description"));
                    break;

                case COMMAND_SESSION_CD:
                    Context.getInstance().setContextItem(ContextEnum.PWD, result.getArgument("path"));
                    break;
                case COMMAND_SESSION_PWD:
                    System.out.println(Context.getInstance().getContextItem(ContextEnum.PWD));
                    break;
                case COMMAND_SESSION_SHOW:
                    Context.getInstance().showContextItems();
                    break;
                case COMMAND_SESSION_SET:
                    Context.getInstance().setContextItemByName(
                            result.getArgument("name"),
                            result.getArgument("value"));
                    break;

                case COMMAND_DR_LIST:
                    DRCommands.getInstance().drList(result.getArgument("path"), result.found("recurse"));
                    break;
                case COMMAND_DR_TREE:
                    int depth = (result.found("depth")) ? Integer.valueOf(result.getArgument("depth")) : 1000000000;
                    DRCommands.getInstance().drTree(result.getArgument("path"), depth);
                    break;
                case COMMAND_DR_DESCRIBE:
                    DRCommands.getInstance().drDescribe(result.getArgument("path"));
                    break;
                case COMMAND_DR_STREAM:
                    DRCommands.getInstance().drStream(result.getArgument("path"));
                    break;

                case COMMAND_PROFILE_CREATE:
                    ProfileCommands.getInstance().profileCreate(
                            result.getArgument("name"),
                            result.getArgument("account"),
                            result.getArgument("biller"));
                    break;

                case COMMAND_PROFILE_DELETE:
                    ProfileCommands.getInstance().profileDelete(result.getArgument("name"));
                    break;

                case COMMAND_PROFILE_SHOW:
                    ProfileCommands.getInstance().profileShow(result.getArgument("name"));
                    break;

                default:
                    throw new IllegalArgumentException("Yikes! We shouldn't be here.");
            }

        } catch (Exception ex) {
            CommandUtils.printErrorAndExit(ex.getMessage() + "\nTry the help command");
        }
    }

    private static Syntax makeSyntax() {
        return new Syntax()
                // -- Dataset Commands --
                .addCommand(new Command()
                        .primaryName("dataset")
                        .secondaryName("create")
                        .commandId(CommandEnum.COMMAND_DATASET_CREATE.getCommandId())
                        .help("Create a new dataset")
                        .addOption(new Option()
                                .shortName("i")
                                .longName("input-json")
                                .hasArgument(true)
                                .optional(false)
                                .help("Path to a file containing the JSON form of a dataset")))
                .addCommand(new Command()
                        .primaryName("dataset")
                        .secondaryName("show")
                        .commandId(CommandEnum.COMMAND_DATASET_SHOW.getCommandId())
                        .help("List one dataset")
                        .addArgument(new Argument()
                                .name("dataset-name")
                                .optional(false)
                                .help("name of the dataset to show")))
                .addCommand(new Command()
                        .primaryName("dataset")
                        .secondaryName("delete")
                        .commandId(CommandEnum.COMMAND_DATASET_DELETE.getCommandId())
                        .help("Delete a dataset")
                        .addArgument(new Argument()
                                .name("dataset-name")
                                .optional(false)
                                .help("Name of the dataset to delete")))
                .addCommand(new Command()
                        .primaryName("dataset")
                        .secondaryName("file")
                        .commandId(CommandEnum.COMMAND_DATASET_FILE.getCommandId())
                        .help("Copy one file into a dataset")
                        .addArgument(new Argument()
                                .name("dataset-name")
                                .optional(false)
                                .help("Name of the dataset where the file should go"))
                        .addOption(new Option()
                                .shortName("p")
                                .longName("profile-id")
                                .hasArgument(true)
                                .optional(true)
                                .help("Identifies the profile to use for allocating storage for the file." +
                                        " Defaults to the dataset profile, if not specified."))
                        .addOption(new Option()
                                .shortName("i")
                                .longName("input-gspath")
                                .hasArgument(true)
                                .optional(false)
                                .help("GCS URI to the source input file"))
                        .addOption(new Option()
                                .shortName("t")
                                .longName("target-path")
                                .hasArgument(true)
                                .optional(true)
                                .help("Target file system path in the dataset. " +
                                        "If not present, the path is derived from the input gspath"))
                        .addOption(new Option()
                                .shortName("m")
                                .longName("mime-type")
                                .hasArgument(true)
                                .optional(true)
                                .help("Mime type of the file"))
                        .addOption(new Option()
                                .shortName("d")
                                .longName("description")
                                .hasArgument(true)
                                .optional(true)
                                .help("Description of the file being copied")))

                // -- session commands --
                .addCommand(new Command()
                        .primaryName("session")
                        .secondaryName("cd")
                        .alternateNames(new String[]{"cd"})
                        .commandId(CommandEnum.COMMAND_SESSION_CD.getCommandId())
                        .help("set the current directory context in jadecli")
                        .addArgument(new Argument()
                                .name("path")
                                .optional(true)
                                .help("Path to a directory in the data repo")))
                .addCommand(new Command()
                        .primaryName("session")
                        .secondaryName("pwd")
                        .alternateNames(new String[]{"pwd"})
                        .commandId(CommandEnum.COMMAND_SESSION_PWD.getCommandId())
                        .help("show the current directory context in jadecli"))
                .addCommand(new Command()
                        .primaryName("session")
                        .secondaryName("show")
                        .commandId(CommandEnum.COMMAND_SESSION_SHOW.getCommandId())
                        .help("show all session properties"))
                .addCommand(new Command()
                        .primaryName("session")
                        .secondaryName("set")
                        .commandId(CommandEnum.COMMAND_SESSION_SET.getCommandId())
                        .help("set a session property")
                        .addArgument(new Argument()
                                .name("name")
                                .optional(false)
                                .help("name of the session property"))
                        .addArgument(new Argument()
                                .name("value")
                                .optional(false)
                                .help("value to assign the session property")))

                // -- hierarchy commands --
                .addCommand(new Command()
                        .primaryName("dr")
                        .secondaryName("list")
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
                        .primaryName("dr")
                        .secondaryName("tree")
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
                        .primaryName("dr")
                        .secondaryName("describe")
                        .alternateNames(new String[]{"describe"})
                        .commandId(CommandEnum.COMMAND_DR_DESCRIBE.getCommandId())
                        .help("describe a data repo object")
                        .addArgument(new Argument()
                                .name("path")
                                .optional(true)
                                .help("Path to an object - sorry, no wildcards yet")))
                .addCommand(new Command()
                        .primaryName("dr")
                        .secondaryName("stream")
                        .alternateNames(new String[]{"cat"})
                        .commandId(CommandEnum.COMMAND_DR_STREAM.getCommandId())
                        .help("stream an object to standard out")
                        .addArgument(new Argument()
                                .name("path")
                                .optional(false)
                                .help("Path to an object")))

                // -- profile commands --
                .addCommand(new Command()
                        .primaryName("profile")
                        .secondaryName("create")
                        .commandId(CommandEnum.COMMAND_PROFILE_CREATE.getCommandId())
                        .help("Create a new profile")
                        .addOption(new Option()
                                .shortName("n")
                                .longName("name")
                                .hasArgument(true)
                                .optional(false)
                                .help("Profile name"))
                        .addOption(new Option()
                                .shortName("a")
                                .longName("account")
                                .hasArgument(true)
                                .optional(false)
                                .help("Billing account"))
                        .addOption(new Option()
                                .shortName("b")
                                .longName("biller")
                                .hasArgument(true)
                                .optional(true)
                                .help("Biller; defaults to 'direct'")))
                .addCommand(new Command()
                        .primaryName("profile")
                        .secondaryName("delete")
                        .commandId(CommandEnum.COMMAND_PROFILE_DELETE.getCommandId())
                        .help("Delete a profile")
                        .addArgument(new Argument()
                                .name("name")
                                .optional(false)
                                .help("Name of the profile to delete")))
                .addCommand(new Command()
                        .primaryName("profile")
                        .secondaryName("show")
                        .commandId(CommandEnum.COMMAND_PROFILE_SHOW.getCommandId())
                        .help("Show a profile")
                        .addArgument(new Argument()
                                .name("name")
                                .optional(true)
                                .help("Name of the profile to show. Defaults to showing all accessible profiles")))

                // -- help command --
                .addCommand(new Command()
                        .primaryName("help")
                        .commandId(CommandEnum.COMMAND_HELP.getCommandId())
                        .help("get help on commands")
                        .addArgument(new Argument()
                                .name("primary")
                                .optional(true)
                                .help("command to get help on"))
                        .addArgument(new Argument()
                                .name("secondary")
                                .optional(true)
                                .help("second word of command to get help on"))
                );

    }
}

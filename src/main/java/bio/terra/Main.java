package bio.terra;

import bio.terra.command.CommandEnum;
import bio.terra.command.CommandUtils;
import bio.terra.command.DRCommands;
import bio.terra.command.DatasetCommands;
import bio.terra.command.HelpCommands;
import bio.terra.command.ProfileCommands;
import bio.terra.command.SessionCommands;
import bio.terra.command.SnapshotCommands;
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

                case COMMAND_SESSION_CD:
                    SessionCommands.sessionCd(result.getArgument("path"));
                    break;
                case COMMAND_SESSION_PWD:
                    SessionCommands.sessionPwd();
                    break;
                case COMMAND_SESSION_SHOW:
                    SessionCommands.sessionShow();
                    break;
                case COMMAND_SESSION_SET:
                    SessionCommands.sessionSet(
                            result.getArgument("name"),
                            result.getArgument("value"));
                    break;

                case COMMAND_SNAPSHOT_CREATE:
                    SnapshotCommands.snapshotCreate(result.getArgument("input-json"));
                    break;
                case COMMAND_SNAPSHOT_SHOW:
                    SnapshotCommands.snapshotShow(result.getArgument("snapshot-name"));
                    break;
                case COMMAND_SNAPSHOT_DELETE:
                    SnapshotCommands.snapshotDelete(result.getArgument("snapshot-name"));
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
                .mergeSyntax(DatasetCommands.getSyntax())
                .mergeSyntax(DRCommands.getSyntax())
                .mergeSyntax(HelpCommands.getSyntax())
                .mergeSyntax(ProfileCommands.getSyntax())
                .mergeSyntax(SessionCommands.getSyntax())
                .mergeSyntax(SnapshotCommands.getSyntax());
    }
}

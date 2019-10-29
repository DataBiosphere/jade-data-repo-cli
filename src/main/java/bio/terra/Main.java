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
            boolean commandHandled = false;

            switch(command) {
                case COMMAND_HELP:
                    commandHandled = HelpCommands.dispatchCommand(command, result, parser);
                    break;

                case COMMAND_DATASET_CREATE:
                case COMMAND_DATASET_SHOW:
                case COMMAND_DATASET_DELETE:
                case COMMAND_DATASET_FILE:
                case COMMAND_DATASET_TABLE:
                case COMMAND_DATASET_POLICY_ADD:
                case COMMAND_DATASET_POLICY_REMOVE:
                case COMMAND_DATASET_POLICY_SHOW:
                    commandHandled = DatasetCommands.dispatchCommand(command, result);
                    break;

                case COMMAND_DR_LIST:
                case COMMAND_DR_TREE:
                case COMMAND_DR_DESCRIBE:
                case COMMAND_DR_STREAM:
                    commandHandled = DRCommands.dispatchCommand(command, result);
                    break;

                case COMMAND_PROFILE_CREATE:
                case COMMAND_PROFILE_DELETE:
                case COMMAND_PROFILE_SHOW:
                    commandHandled = ProfileCommands.dispatchCommand(command, result);
                    break;

                case COMMAND_SESSION_CD:
                case COMMAND_SESSION_PWD:
                case COMMAND_SESSION_SHOW:
                case COMMAND_SESSION_SET:
                    commandHandled = SessionCommands.dispatchCommand(command, result);
                    break;

                case COMMAND_SNAPSHOT_CREATE:
                case COMMAND_SNAPSHOT_SHOW:
                case COMMAND_SNAPSHOT_DELETE:
                case COMMAND_SNAPSHOT_POLICY_ADD:
                case COMMAND_SNAPSHOT_POLICY_REMOVE:
                case COMMAND_SNAPSHOT_POLICY_SHOW:
                    commandHandled = SnapshotCommands.dispatchCommand(command, result);
                    break;

                default:
                    commandHandled = false;
            }

            if (!commandHandled) {
                throw new IllegalArgumentException("Unhandled command! Yikes!! We shouldn't be here.");
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

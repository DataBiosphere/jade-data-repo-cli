package bio.terra.command;

import bio.terra.context.Context;
import bio.terra.context.ContextEnum;
import bio.terra.parser.Argument;
import bio.terra.parser.Command;
import bio.terra.parser.ParsedResult;
import bio.terra.parser.Syntax;

public class SessionCommands {

    private static SessionCommands theProfileCommands;

    private SessionCommands() {
    }

    public static SessionCommands getInstance() {
        if (theProfileCommands == null) {
            theProfileCommands = new SessionCommands();
        }
        return theProfileCommands;
    }

    public static Syntax getSyntax() {
        return new Syntax()
                .addCommand(new Command()
                        .primaryNames(new String[]{"session", "cd"})
                        .alternateNames(new String[]{"cd"})
                        .commandId(CommandEnum.COMMAND_SESSION_CD.getCommandId())
                        .help("set the current directory context in jadecli")
                        .addArgument(new Argument()
                                .name("path")
                                .optional(true)
                                .help("Path to a directory in the data repo")))
                .addCommand(new Command()
                        .primaryNames(new String[]{"session", "pwd"})
                        .alternateNames(new String[]{"pwd"})
                        .commandId(CommandEnum.COMMAND_SESSION_PWD.getCommandId())
                        .help("show the current directory context in jadecli"))
                .addCommand(new Command()
                        .primaryNames(new String[]{"session", "show"})
                        .commandId(CommandEnum.COMMAND_SESSION_SHOW.getCommandId())
                        .help("show all session properties"))
                .addCommand(new Command()
                        .primaryNames(new String[]{"session", "set"})
                        .commandId(CommandEnum.COMMAND_SESSION_SET.getCommandId())
                        .help("set a session property")
                        .addArgument(new Argument()
                                .name("name")
                                .optional(false)
                                .help("name of the session property"))
                        .addArgument(new Argument()
                                .name("value")
                                .optional(false)
                                .help("value to assign the session property")));
    }
    public static boolean dispatchCommand(CommandEnum command, ParsedResult result) {
        switch (command) {
            case COMMAND_SESSION_CD:
                sessionCd(result.getArgument("path"));
                break;
            case COMMAND_SESSION_PWD:
                sessionPwd();
                break;
            case COMMAND_SESSION_SHOW:
                sessionShow();
                break;
            case COMMAND_SESSION_SET:
                sessionSet(
                        result.getArgument("name"),
                        result.getArgument("value"));
                break;
            default:
                return false;
        }

        return true;
    }

    private static void sessionCd(String path) {
        Context.getInstance().setContextItem(ContextEnum.PWD, path);
    }

    private static void sessionPwd() {
        System.out.println(Context.getInstance().getContextItem(ContextEnum.PWD));
    }

    private static void sessionShow() {
        Context.getInstance().showContextItems();
    }

    private static void sessionSet(String name, String value) {
        Context.getInstance().setContextItemByName(name, value);
    }
}

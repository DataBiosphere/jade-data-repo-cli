package bio.terra.command;

import bio.terra.context.Context;
import bio.terra.context.ContextEnum;
import bio.terra.parser.Argument;
import bio.terra.parser.Command;
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
                                .help("value to assign the session property")));
    }

    public static void sessionCd(String path) {
        Context.getInstance().setContextItem(ContextEnum.PWD, path);
    }

    public static void sessionPwd() {
        System.out.println(Context.getInstance().getContextItem(ContextEnum.PWD));
    }

    public static void sessionShow() {
        Context.getInstance().showContextItems();
    }

    public static void sessionSet(String name, String value) {
        Context.getInstance().setContextItemByName(name, value);
    }
}

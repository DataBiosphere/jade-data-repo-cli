package bio.terra.command;

import bio.terra.context.Context;
import bio.terra.context.ContextAuthTypeEnum;
import bio.terra.context.ContextEnum;
import bio.terra.context.Login;
import bio.terra.parser.Command;
import bio.terra.parser.Option;
import bio.terra.parser.ParsedResult;
import bio.terra.parser.Syntax;

public final class AuthCommands {

  private static AuthCommands theAuthCommands;

  private AuthCommands() {}

  public static AuthCommands getInstance() {
    if (theAuthCommands == null) {
      theAuthCommands = new AuthCommands();
    }
    return theAuthCommands;
  }

  public static Syntax getSyntax() {
    return new Syntax()
        .addCommand(
            new Command()
                .primaryNames(new String[] {"auth", "login"})
                .commandId(CommandEnum.COMMAND_AUTH_LOGIN.getCommandId())
                .help("authenticate user via browser and save the context"))
        .addCommand(
            new Command()
                .primaryNames(new String[] {"auth", "sa"})
                .commandId(CommandEnum.COMMAND_AUTH_SA.getCommandId())
                .help("authenticate service account via a key file")
                .addOption(
                    new Option()
                        .shortName("k")
                        .longName("key-file")
                        .hasArgument(true)
                        .optional(false)
                        .help("path to JSON key file for the service account")));
  }

  public static boolean dispatchCommand(CommandEnum command, ParsedResult result) {
    switch (command) {
      case COMMAND_AUTH_LOGIN:
        authLogin();
        break;
      case COMMAND_AUTH_SA:
        authSA(result.getArgument("key-file"));
        break;
      default:
        return false;
    }

    return true;
  }

  private static void authLogin() {
    Context.getInstance()
        .setContextItem(
            ContextEnum.AUTH_TYPE, ContextAuthTypeEnum.AUTH_TYPE_USER.getContextValue());
    if (!Login.clearCredentialDirectory()) {
      CommandUtils.printErrorAndExit("Unable to remove old credentials");
    }
    Login.requiresLogin();
  }

  private static void authSA(String keyFile) {
    Context.getInstance()
        .setContextItem(ContextEnum.AUTH_TYPE, ContextAuthTypeEnum.AUTH_TYPE_SA.getContextValue());
    Context.getInstance().setContextItem(ContextEnum.AUTH_KEY_FILE, keyFile);
    Login.requiresLogin();
  }
}

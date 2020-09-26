package bio.terra.command;

import bio.terra.parser.Argument;
import bio.terra.parser.Command;
import bio.terra.parser.ParsedResult;
import bio.terra.parser.Parser;
import bio.terra.parser.Syntax;
import java.util.ArrayList;
import java.util.List;

public final class HelpCommands {
  // If we have syntax with commands of more than 4 primary names, just update this constant to
  // allow
  // the help command to parse more.
  private static final int CMDNAME_COUNT = 4;

  private HelpCommands() {}

  public static Syntax getSyntax() {
    Command helpCommand =
        new Command()
            .primaryNames(new String[] {"help"})
            .commandId(CommandEnum.COMMAND_HELP.getCommandId())
            .help("get help on commands");

    for (int i = 1; i <= CMDNAME_COUNT; i++) {
      helpCommand.addArgument(
          new Argument().name("cmdname" + i).optional(true).help("command to get help on"));
    }

    return new Syntax().addCommand(helpCommand);
  }

  private static void addCmdName(List<String> cmdNames, ParsedResult result, int idx) {
    String cmd = result.getArgument("cmdname" + idx);
    if (cmd != null) {
      cmdNames.add(cmd);
    }
  }

  public static boolean dispatchCommand(CommandEnum command, ParsedResult result, Parser parser) {
    List<String> cmdNames = new ArrayList<>();
    for (int i = 1; i <= CMDNAME_COUNT; i++) {
      addCmdName(cmdNames, result, i);
    }

    if (cmdNames.size() == 0) {
      parser.printAllCommandHelp(System.out);
    } else {
      parser.printCommandHelp(System.out, cmdNames);
    }
    return true;
  }
}

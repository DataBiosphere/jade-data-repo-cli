package bio.terra.parser;

import java.util.ArrayList;
import java.util.List;

public class Syntax {
  private List<Command> commands;

  public Syntax() {
    this.commands = new ArrayList<>();
  }

  public Syntax addCommand(Command command) {
    commands.add(command);
    return this;
  }

  public Syntax mergeSyntax(Syntax other) {
    commands.addAll(other.getCommands());
    return this;
  }

  public void parse(ParseContext context) {
    String arg = context.getArg();
    if (arg == null) {
      throw new IllegalArgumentException("No command found");
    }
    for (Command command : commands) {
      if (command.parse(context)) {
        return;
      }
    }

    throw new IllegalArgumentException("Invalid command: " + context.getCommandLine());
  }

  public List<Command> getCommands() {
    return commands;
  }

  public Syntax commands(List<Command> commands) {
    this.commands = commands;
    return this;
  }
}

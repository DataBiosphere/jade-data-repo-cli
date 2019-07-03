package bio.terra.parser;

import org.apache.commons.lang3.StringUtils;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private Syntax syntax;

    public Parser(Syntax syntax) {
        this.syntax = syntax;
    }

    public ParsedResult parse(String[] args) {
        ParseContext context = new ParseContext(args);
        try {
            syntax.parse(context);
        } catch (ParserException ex) {
            System.err.println("Error: " + ex.getMessage());
            int commandId = context.getResult().getCommandId();
            // If we have a command, print the single line help for it
            Command command = lookupCommandById(commandId);
            if (command != null) {
                System.err.print("Help: ");
                command.printHelpLine(System.err);
                System.exit(1);
            }
            return null;
        }
        return context.getResult();
    }

    public void printAllCommandHelp(PrintStream ps) {
        printListCommandHelp(ps, syntax.getCommands());
    }

    // Print help for a list of commands
    public void printListCommandHelp(PrintStream ps, List<Command> commands) {
        for (Command command : commands) {
            ps.printf("  %-10s %-10s - %s\n",
                    command.getPrimaryName(),
                    (StringUtils.isEmpty(command.getSecondaryName()) ? "" : command.getSecondaryName()),
                    command.getHelp());
        }
    }

    public void printCommandHelp(PrintStream ps, String primaryName, String secondaryName) {
        // There are several cases that are a little hard to discern
        // Case 1 is where we have a primary name or primary/secondary or abbreviation that is a specific command
        // We want to give detailed command help in that case.
        //
        // Case 2 is where we have a primary name that is a category and a set of
        // secondary names that make specific commands. In that case we want to give
        // the list of secondaries.
        Command command = lookupCommand(primaryName, secondaryName);
        if (command == null) {
            List<Command> commandList = lookupCommandGroup(primaryName, secondaryName);
            if (commandList == null) {
                ps.println("No such command: " + primaryName + " " + secondaryName);
            } else {
                printListCommandHelp(ps, commandList);
            }
        } else {
            command.printHelp(ps);
        }
    }

    private Command lookupCommand(String primaryName, String secondaryName) {
        for (Command command : syntax.getCommands()) {
            if (StringUtils.equalsIgnoreCase(primaryName, command.getPrimaryName()) &&
                    StringUtils.equalsIgnoreCase(secondaryName, command.getSecondaryName())) {
                return command;
            }
            if (command.getAlternateNames() != null) {
                for (String name : command.getAlternateNames()) {
                    if (StringUtils.equalsIgnoreCase(name, primaryName)) {
                        return command;
                    }
                }
            }
        }
        return null;
    }

    private Command lookupCommandById(int commandId) {
        for (Command command : syntax.getCommands()) {
            if (command.getCommandId() == commandId) {
                return command;
            }
        }
        return null;
    }


    private List<Command> lookupCommandGroup(String primaryName, String secondaryNamme) {
        if (!StringUtils.isEmpty(secondaryNamme)) {
            return null;
        }
        List<Command> commandList = new ArrayList<>();
        for (Command command : syntax.getCommands()) {
            if (StringUtils.equalsIgnoreCase(primaryName, command.getPrimaryName())) {
                commandList.add(command);
            }
        }
        return commandList;
    }

}

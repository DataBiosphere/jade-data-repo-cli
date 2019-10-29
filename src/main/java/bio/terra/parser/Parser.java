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
        // Compute the max names we will show, so we can get them nicely lined up
        int maxPrimaryNames = 0;
        for (Command command : commands) {
            if (maxPrimaryNames < command.getPrimaryNames().length) {
                maxPrimaryNames = command.getPrimaryNames().length;
            }
        }

        for (Command command : commands) {
            // For each command, for number of columns fill in either name or blank
            StringBuffer sb = new StringBuffer("  ");
            for (int i = 0; i < maxPrimaryNames; i++) {
                String c = StringUtils.EMPTY;
                if (i < command.getPrimaryNames().length - 1) {
                    c = command.getPrimaryNames()[i];
                }
                String.format("%-10s ", c);
            }
            sb.append("- ").append(command.getHelp());
            ps.println(sb.toString());
        }
    }

    public void printCommandHelp(PrintStream ps, List<String> cmdNames) {
        // Get a list of all commands that have cmdNames as the start of their primary names
        // Three results:
        //  1. no match - error
        //  2. match one command - provide detailed help
        //  3. match several commands - list the set of commands
        List<Command> commandList = lookupCommands(cmdNames);
        switch (commandList.size()) {
            case 0: {
                StringBuffer msg = new StringBuffer("No such command:");
                for (String cmd : cmdNames) {
                    msg.append(" ").append(cmd);
                }
                ps.println(msg.toString());
                break;
            }

            case 1:
                commandList.get(0).printHelp(ps);
                break;

            default: // > 1
                printListCommandHelp(ps, commandList);
                break;
        }
    }

    // Lookup all commands that have cmdNames as the prefix
    private List<Command> lookupCommands(List<String> cmdNames) {
        List<Command> commandList = new ArrayList<>();
        for (Command command : syntax.getCommands()) {
            if (commandMatches(cmdNames, command)) {
                commandList.add(command);
            } else {
                // If we find an exact alternate name match, we just return that.
                // TODO: This assumes the untested axiom that alternate names do not
                //  overlap with the first name of any command.
                if (cmdNames.size() == 1 && command.getAlternateNames() != null) {
                    for (String name : command.getAlternateNames()) {
                        if (StringUtils.equalsIgnoreCase(name, cmdNames.get(0))) {
                            commandList.add(command);
                            return commandList;
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean commandMatches(List<String> cmdNames, Command command) {
        String[] primaryNames = command.getPrimaryNames();
        if (cmdNames.size() <= primaryNames.length) {
            return false;
        }
        for (int i = 0; i < cmdNames.size(); i++) {
            if (!StringUtils.equalsIgnoreCase(cmdNames.get(i), primaryNames[i])) {
                return false;
            }
        }
        return true;
    }

    private Command lookupCommandById(int commandId) {
        for (Command command : syntax.getCommands()) {
            if (command.getCommandId() == commandId) {
                return command;
            }
        }
        return null;
    }
}

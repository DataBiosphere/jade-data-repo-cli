package bio.terra.parser;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Command {
    private String primaryName;
    private String secondaryName;
    private String[] alternateNames;
    private int commandId;
    private String help;
    private List<Option> options;
    private List<Argument> arguments;

    public Command() {
        this.options = new ArrayList<>();
        this.arguments = new ArrayList<>();
    }

    public boolean parse(ParseContext context) {
        // See if this is our command
        if (!matchName(context)) {
            return false;
        }

        context.getResult().setCommand(commandId);

        // Parse switches until no more switches
        if (options.size() > 0) {
            while (context.argIsSwitch()) {
                boolean found = false;
                for (Option option : options) {
                    if (option.parse(context)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new ParserException("Unknown option " + context.getArg());
                }
            }

            // Validate that all required switches are present
            for (Option option : options) {
                if (!option.isOptional()) {
                    if (!context.getResult().found(option.getLongName())) {
                        throw new ParserException("Option " + option.getSwitchName() + " is required");
                    }
                }
            }
        }

        // Collect arguments in order
        for (Argument argument : arguments) {
            argument.parse(context);
        }

        return true;
    }

    // If the name matches, then we are shifted past the name.
    // Otherwise, the parsing position stays where it was.
    private boolean matchName(ParseContext context) {
        String testName = context.getArg();
        if (StringUtils.equalsIgnoreCase(testName, primaryName)) {
            if (secondaryName == null) {
                context.shift();
                return true;
            }

            String testName2 = context.peekArg();
            if (StringUtils.equalsIgnoreCase(testName2, secondaryName)) {
                context.shift();
                context.shift();
                return true;
            }
        }
        if (alternateNames != null) {
            for (String altname : alternateNames) {
                if (StringUtils.equalsIgnoreCase(testName, altname)) {
                    context.shift();
                    return true;
                }
            }
        }
        return false;
    }

    public Command addOption(Option option) {
        options.add(option);
        return this;
    }

    public Command addArgument(Argument argument) {
        arguments.add(argument);
        return this;
    }

    public String getPrimaryName() {
        return primaryName;
    }

    public Command primaryName(String primaryName) {
        this.primaryName = primaryName;
        return this;
    }

    public String getSecondaryName() {
        return secondaryName;
    }

    public Command secondaryName(String secondaryName) {
        this.secondaryName = secondaryName;
        return this;
    }

    public int getCommandId() {
        return commandId;
    }

    public Command commandId(int commandId) {
        this.commandId = commandId;
        return this;
    }

    public String[] getAlternateNames() {
        return alternateNames;
    }

    public Command alternateNames(String[] alternateNames) {
        this.alternateNames = alternateNames;
        return this;
    }

    public String getHelp() {
        return help;
    }

    public Command help(String help) {
        this.help = help;
        return this;
    }

    public List<Option> getOptions() {
        return options;
    }

    public Command options(List<Option> options) {
        this.options = options;
        return this;
    }

    public void printHelpLine(PrintStream ps) {
        StringBuffer sb = new StringBuffer().append(primaryName);
        if (secondaryName !=null) {
            sb.append(" ").append(secondaryName);
        }

        for (Option option : options) {
            if (option.isOptional()) {
                sb.append(" [");
            }
            sb.append(" ").append(option.getSwitchName());
            if (option.isHasArgument()) {
                sb.append(" <").append(option.getArgName()).append('>');
            }
            if (option.isOptional()) {
                sb.append(" ]");
            }
        }

        for (Argument argument :arguments) {
            if (argument.isOptional()) {
                sb.append(" [");
            }
            sb.append(" <").append(argument.getName()).append('>');
            if (argument.isOptional()) {
                sb.append(" ]");
            }
        }
        ps.println(sb.toString());
    }

    public void printHelp(PrintStream ps) {
        printHelpLine(ps);
        String fmt = "    %-16s    %s\n";
        if (options.size() > 0) {
            ps.println("  Options:");
            for (Option option : options) {
                String switches = "-" + option.getShortName();
                if (option.getLongName() != null) {
                    switches = switches + ", --" + option.getLongName();
                }
                ps.printf(fmt, switches, option.getHelp());
            }
        }

        if (arguments.size() > 0) {
            ps.println("  Arguments:");
            for (Argument argument : arguments) {
                String arg = '<' + argument.getName() + '>';
                ps.printf(fmt, arg, argument.getHelp());
            }
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("primaryName", primaryName)
                .append("secondaryName", secondaryName)
                .append("alternateNames", alternateNames)
                .append("commandId", commandId)
                .append("help", help)
                .append("options", options)
                .append("arguments", arguments)
                .toString();
    }
}

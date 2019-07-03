package bio.terra.parser;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

// Option represents a switch like -x or --xyz with perhaps an argument.
// Short name is required. Long name is optional.
public class Option {
    private String shortName;
    private String longName;
    private String help;
    private boolean optional;
    private boolean hasArgument;

    public Option() {
    }

    public boolean parse(ParseContext context) {
        String arg = context.getArg();
        if ((StringUtils.startsWith(arg, "--") && StringUtils.equals(longName, StringUtils.substring(arg, 2))) ||
                (StringUtils.startsWith(arg, "-") && StringUtils.equals(shortName, StringUtils.substring(arg, 1)))) {
            // We match!
            context.shift();
            String optionArg = null;
            if (hasArgument) {
                optionArg = context.getArg();
                if (optionArg == null) {
                    throw new ParserException("Option " + arg + " requires an argument");
                }
                context.shift();
            }

            context.getResult().setArgument(getArgName(), optionArg);
            return true;
        }
        return false;
    }

    public String getArgName() {
        return (longName != null) ? longName : shortName;
    }

    public String getSwitchName() {
        if (longName != null) {
            return "--" + longName;
        }
        return "-" + shortName;
    }

    public String getShortName() {
        return shortName;
    }

    public Option shortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public String getLongName() {
        return longName;
    }

    public Option longName(String longName) {
        this.longName = longName;
        return this;
    }

    public String getHelp() {
        return help;
    }

    public Option help(String help) {
        this.help = help;
        return this;
    }

    public boolean isOptional() {
        return optional;
    }

    public Option optional(boolean optional) {
        this.optional = optional;
        return this;
    }

    public boolean isHasArgument() {
        return hasArgument;
    }

    public Option hasArgument(boolean hasArgument) {
        this.hasArgument = hasArgument;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("shortName", shortName)
                .append("longName", longName)
                .append("help", help)
                .append("optional", optional)
                .toString();
    }
}

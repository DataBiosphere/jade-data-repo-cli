package bio.terra.parser;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ParseContext {
    private ParsedResult result;
    private String[] args;
    private int currentIndex;

    public ParseContext(String[] args) {
        this.args = args;
        currentIndex = 0;
        result = new ParsedResult();
    }

    public String getArg() {
        if (args.length > currentIndex) {
            return args[currentIndex];
        }
        return null;
    }

    public String peekArg() {
        currentIndex++;
        String arg = getArg();
        currentIndex--;
        return arg;
    }

    public boolean argIsSwitch() {
        String arg = getArg();
        return (StringUtils.startsWith(arg, "--") || StringUtils.startsWith(arg, "-"));
    }

    public void shift() {
        currentIndex++;
    }

    public ParsedResult getResult() {
        return result;
    }

    public String getCommandLine() {
        return StringUtils.join(args, ' ');
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("result", result)
                .append("args", args)
                .append("currentIndex", currentIndex)
                .toString();
    }
}

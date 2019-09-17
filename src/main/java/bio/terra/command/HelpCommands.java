package bio.terra.command;

import bio.terra.parser.Argument;
import bio.terra.parser.Command;
import bio.terra.parser.Parser;
import bio.terra.parser.Syntax;
import org.apache.commons.lang3.StringUtils;

public class HelpCommands {
    Parser parser;

    public static Syntax getSyntax() {
        return new Syntax()
                .addCommand(new Command()
                        .primaryName("help")
                        .commandId(CommandEnum.COMMAND_HELP.getCommandId())
                        .help("get help on commands")
                        .addArgument(new Argument()
                                .name("primary")
                                .optional(true)
                                .help("command to get help on"))
                        .addArgument(new Argument()
                                .name("secondary")
                                .optional(true)
                                .help("second word of command to get help on")));
    }

    public HelpCommands(Parser parser) {
        this.parser = parser;
    }

    public void helpCommand(String primaryName, String secondaryName) {
        if (StringUtils.isEmpty(primaryName)) {
            parser.printAllCommandHelp(System.out);
        } else {
            parser.printCommandHelp(System.out, primaryName, secondaryName);
        }
    }

}

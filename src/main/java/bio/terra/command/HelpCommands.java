package bio.terra.command;

import bio.terra.parser.Parser;
import org.apache.commons.lang3.StringUtils;

public class HelpCommands {
    Parser parser;

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

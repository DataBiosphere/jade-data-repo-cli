package bio.terra.parser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ParserTest {

    @Test
    public void testLs() throws Exception {
        Parser parser = new Parser(buildSyntax());

        String[] testString = new String[]{"ls", "filepath"};
        ParsedResult result = parser.parse(testString);
        assertThat("valid command", result.getCommandId(), equalTo(1));
        assertThat("valid argument", result.getArgument("path"), equalTo("filepath"));

        testString = new String[]{"ls"};
        result = parser.parse(testString);
        assertThat("valid command", result.getCommandId(), equalTo(1));
        assertNull("null argument", result.getArgument("path"));
    }


    @Test
    public void testSwitches() throws Exception {
        Parser parser = new Parser(buildSyntax());

        // parse everything
        String[] testString = new String[]{"test", "-a", "--rrr", "grrr", "-x", "xarg", "thereqarg", "theoptarg"};
        ParsedResult result = parser.parse(testString);
        assertThat("valid command", result.getCommandId(), equalTo(2));
        assertTrue("valid switch -a", result.found("aaa"));
        assertNull("no argument for -a", result.getArgument("aaa"));
        assertTrue("valid switch -r", result.found("rrr"));
        assertThat("valid argument -r", result.getArgument("rrr"), equalTo("grrr"));
        assertTrue("valid switch -x", result.found("x"));
        assertThat("valid argument -x", result.getArgument("x"), equalTo("xarg"));
        assertTrue("valid reqarg", result.found("reqarg"));
        assertThat("valid argument reqarg", result.getArgument("reqarg"), equalTo("thereqarg"));
        assertTrue("valid optarg", result.found("optarg"));
        assertThat("valid argument optarg", result.getArgument("optarg"), equalTo("theoptarg"));

        // leave out optionals, re-order switches
        testString = new String[]{"test", "-x", "xarg", "--rrr", "grrr", "thereqarg"};
        result = parser.parse(testString);
        assertThat("valid command", result.getCommandId(), equalTo(2));
        assertFalse("valid switch -a", result.found("aaa"));
        assertTrue("valid switch -r", result.found("rrr"));
        assertThat("valid argument -r", result.getArgument("rrr"), equalTo("grrr"));
        assertTrue("valid switch -x", result.found("x"));
        assertThat("valid argument -x", result.getArgument("x"), equalTo("xarg"));
        assertTrue("valid reqarg", result.found("reqarg"));
        assertThat("valid argument reqarg", result.getArgument("reqarg"), equalTo("thereqarg"));
        assertFalse("valid optarg", result.found("optarg"));

        // try a two part name and an abbreviation
        testString = new String[]{"test2", "again"};
        result = parser.parse(testString);
        assertThat("valid command", result.getCommandId(), equalTo(3));

        testString = new String[]{"testtube", "again"};
        result = parser.parse(testString);
        assertThat("valid command", result.getCommandId(), equalTo(3));
        assertThat("valid argument myarg", result.getArgument("myarg"), equalTo("again"));
    }

    @Test
    public void testMissing() {

        Parser parser = new Parser(buildSyntax());

        // Missing required switch
        String[] testString = new String[]{"test", "-x", "xarg", "thereqarg", "theoptarg"};
        ParsedResult result = parser.parse(testString);
        assertNull("caught error", result);

        // Missing required arg
        testString = new String[]{"test", "-r", "grrr"};
        result = parser.parse(testString);
        assertNull("caught error", result);
    }

    private Syntax buildSyntax() {
        return new Syntax()
                .addCommand(new Command()
                        .primaryName("ls")
                        .commandId(1)
                        .alternateNames(new String[]{"dir"})
                        .help("List files or directories")
                        .addArgument(new Argument()
                                .name("path")
                                .optional(true)
                                .help("Path to file or directory; no wildcards")))
                .addCommand(new Command()
                        .primaryName("test")
                        .commandId(2)
                        .help("No help for you!")
                        .addOption(new Option()
                                .shortName("a")
                                .longName("aaa")
                                .hasArgument(false)
                                .optional(true)
                                .help("aaa"))
                        .addOption(new Option()
                                .shortName("r")
                                .longName("rrr")
                                .hasArgument(true)
                                .optional(false)
                                .help("rrr"))
                        .addOption(new Option()
                                .shortName("x")
                                .hasArgument(true)
                                .optional(true)
                                .help("xxx"))
                        .addArgument(new Argument()
                                .name("reqarg")
                                .optional(false)
                                .help("reqarg"))
                        .addArgument(new Argument()
                                .name("optarg")
                                .optional(true)
                                .help("optarg")))
                .addCommand(new Command()
                        .primaryName("test2")
                        .secondaryName("again")
                        .commandId(3)
                        .alternateNames(new String[]{"testtube"})
                        .addArgument(new Argument()
                                .name("myarg")
                                .optional(true)));
    }

}

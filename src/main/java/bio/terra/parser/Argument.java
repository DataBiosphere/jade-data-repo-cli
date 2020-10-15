package bio.terra.parser;

// Arguments are simply pulled from the arglist; the name is not parsed, but is used in help and to
// lookup the argument value. It should not overlap with any other keyword.
public class Argument {
  private String name;
  private String help;
  private boolean optional;

  public Argument() {}

  public void parse(ParseContext context) {
    String arg = context.getArg();
    if (arg != null) {
      context.shift();
      context.getResult().setArgument(name, arg);
    } else {
      if (!optional) {
        throw new ParserException("Required argument " + name + " is missing");
      }
    }
  }

  public String getName() {
    return name;
  }

  public Argument name(String name) {
    this.name = name;
    return this;
  }

  public String getHelp() {
    return help;
  }

  public Argument help(String help) {
    this.help = help;
    return this;
  }

  public boolean isOptional() {
    return optional;
  }

  public Argument optional(boolean optional) {
    this.optional = optional;
    return this;
  }
}

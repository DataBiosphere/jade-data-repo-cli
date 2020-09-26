package bio.terra.parser;

import java.util.HashMap;
import java.util.Map;

public class ParsedResult {
  private int commandId;
  private Map<String, String> arguments;

  public ParsedResult() {
    commandId = -1;
    arguments = new HashMap<>();
  }

  public int getCommandId() {
    return commandId;
  }

  public void setCommand(int commandId) {
    this.commandId = commandId;
  }

  public boolean found(String key) {
    return arguments.containsKey(key);
  }

  public String getArgument(String key) {
    return arguments.get(key);
  }

  public void setArgument(String name, String argumentValue) {
    arguments.put(name, argumentValue);
  }
}

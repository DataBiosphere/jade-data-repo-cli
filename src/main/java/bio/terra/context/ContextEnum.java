package bio.terra.context;

public enum ContextEnum {
  PWD("pwd", "/"),
  BASE_PATH("basepath", "http://localhost:8080"),
  // TODO: remove this, I think
  //  PROJECT_ID("projectid", "broad-jade-dev"),
  AUTH_TYPE("authtype", "user"), // or sa
  AUTH_KEY_FILE("authkeyfile", "");

  private final String key;
  private final String defaultValue;

  ContextEnum(String key, String defaultValue) {
    this.key = key;
    this.defaultValue = defaultValue;
  }

  public String getKey() {
    return key;
  }

  public String getDefaultValue() {
    return defaultValue;
  }
}

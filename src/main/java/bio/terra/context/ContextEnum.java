package bio.terra.context;

public enum ContextEnum {
  PWD("pwd", "/"),
  BASE_PATH("basepath", "http://localhost:8080"),
  PROJECT_ID("projectid", "broad-jade-dd"),
  APPLICATION_NAME("applicationname", "jade-data-repo");

  private String key;
  private String defaultValue;

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

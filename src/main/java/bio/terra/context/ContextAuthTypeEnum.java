package bio.terra.context;

import org.apache.commons.lang3.StringUtils;

public enum ContextAuthTypeEnum {
  AUTH_TYPE_USER("user"),
  AUTH_TYPE_SA("sa");

  private String contextValue;

  ContextAuthTypeEnum(String contextValue) {
    this.contextValue = contextValue;
  }

  public static ContextAuthTypeEnum fromContextValue(String value) {
    if (StringUtils.equalsIgnoreCase(value, AUTH_TYPE_SA.contextValue)) {
      return AUTH_TYPE_SA;
    }
    return AUTH_TYPE_USER;
  }

  public String getContextValue() {
    return contextValue;
  }
}

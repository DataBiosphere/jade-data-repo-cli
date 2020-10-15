package bio.terra.context;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;

public final class Context {
  private static final String CONTEXT_STORE_PATH = ".jadecli/context.properties";

  // TODO: hold some list of previous paths and allow selection
  //    private static String BASE_PATH = "https://jade.datarepo-dev.broadinstitute.org";
  // TODO: get project id from study/dataset when querying

  private static Context theContext;

  private Properties properties;
  private File propertiesFile;

  private Context() {
    properties = new Properties();
    propertiesFile = new File(System.getProperty("user.home"), CONTEXT_STORE_PATH);
    getContext();
  }

  public static Context getInstance() {
    if (theContext == null) {
      theContext = new Context();
    }
    return theContext;
  }

  @SuppressFBWarnings(
      value = "OBL_UNSATISFIED_OBLIGATION",
      justification = "Spotbugs bug: not properly understanding the resource try")
  private void getContext() {
    try (FileInputStream fileInputStream = new FileInputStream(propertiesFile)) {
      properties.load(fileInputStream);
    } catch (FileNotFoundException ex) {
      for (ContextEnum contextEnum : ContextEnum.values()) {
        properties.setProperty(contextEnum.getKey(), contextEnum.getDefaultValue());
      }
      putContext();
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to load properties from " + propertiesFile.getPath());
    }
  }

  private void putContext() {
    try (OutputStream output = new FileOutputStream(propertiesFile)) {
      properties.store(output, null);
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to store properties to " + propertiesFile.getPath());
    }
  }

  // -- accessors --

  public String getContextItem(ContextEnum contextEnum) {
    return properties.getProperty(contextEnum.getKey());
  }

  public void setContextItem(ContextEnum contextEnum, String value) {
    properties.setProperty(contextEnum.getKey(), value);
    putContext();
  }

  public void setContextItemByName(String itemName, String value) {
    for (ContextEnum contextEnum : ContextEnum.values()) {
      String key = contextEnum.getKey();
      if (StringUtils.equalsIgnoreCase(key, itemName)) {
        setContextItem(contextEnum, value);
        return;
      }
    }
    throw new IllegalArgumentException("No session property named '" + itemName + "'");
  }

  public void showContextItems() {
    for (ContextEnum contextEnum : ContextEnum.values()) {
      String key = contextEnum.getKey();
      System.out.printf("  %-18s: %s%n", key, properties.getProperty(key));
    }
  }
}

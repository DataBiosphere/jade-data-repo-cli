package bio.terra.command;

import bio.terra.context.Context;
import bio.terra.context.ContextEnum;
import bio.terra.datarepo.model.BillingProfileModel;
import bio.terra.datarepo.model.DatasetSummaryModel;
import bio.terra.datarepo.model.EnumerateBillingProfileModel;
import bio.terra.datarepo.model.EnumerateDatasetModel;
import bio.terra.datarepo.model.EnumerateSnapshotModel;
import bio.terra.datarepo.model.PolicyModel;
import bio.terra.datarepo.model.PolicyResponse;
import bio.terra.datarepo.model.SnapshotSummaryModel;
import bio.terra.parser.Option;
import bio.terra.tdrwrapper.exception.DataRepoClientException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public final class CommandUtils {

  public static final String SLASH = "/";
  public static final String AT = "@";

  private static final ObjectMapper objectMapper = new ObjectMapper();

  // Common format option
  public static final Option formatOption =
      new Option()
          .longName("format")
          .hasArgument(true)
          .optional(true)
          .help("Output format; 'text' is the default; 'json' is supported");

  private CommandUtils() {}

  public static ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  public static void printError(DataRepoClientException ex) {
    System.out.printf("[%d] %s%n", ex.getStatusCode(), ex.getMessage());
    if (ex.getErrorDetails() != null) {
      for (String detail : ex.getErrorDetails()) {
        System.out.printf("  %s%n", detail);
      }
    }
  }

  public static void printErrorAndExit(String message) {
    System.err.println(message);
    System.exit(1);
  }

  public static DatasetSummaryModel findDatasetByName(String datasetName) {
    try {
      EnumerateDatasetModel enumerateDataset =
          DRApi.get().enumerateDatasets(0, 100000, null, null, datasetName);

      List<DatasetSummaryModel> studies = enumerateDataset.getItems();
      for (DatasetSummaryModel summary : studies) {
        if (StringUtils.equals(summary.getName(), datasetName)) {
          return summary;
        }
      }
      CommandUtils.printErrorAndExit("Dataset not found: " + datasetName);
      return null;

    } catch (DataRepoClientException ex) {
      throw new IllegalArgumentException("Error processing find dataset by name");
    }
  }

  public static SnapshotSummaryModel findSnapshotByName(String snapshotName) {
    try {
      EnumerateSnapshotModel enumerateSnapshot =
          DRApi.get().enumerateSnapshots(0, 100000, null, null, snapshotName);

      List<SnapshotSummaryModel> studies = enumerateSnapshot.getItems();
      for (SnapshotSummaryModel summary : studies) {
        if (StringUtils.equals(summary.getName(), snapshotName)) {
          return summary;
        }
      }
      CommandUtils.printErrorAndExit("Snapshot not found: " + snapshotName);
      return null;

    } catch (DataRepoClientException ex) {
      throw new IllegalArgumentException("Error processing find snapshot by name");
    }
  }

  public static BillingProfileModel findProfileByName(String profileName) {
    try {
      EnumerateBillingProfileModel enumerateProfiles = DRApi.get().enumerateProfiles(0, 100000);

      List<BillingProfileModel> profiles = enumerateProfiles.getItems();
      for (BillingProfileModel profile : profiles) {
        if (StringUtils.equals(profile.getProfileName(), profileName)) {
          return profile;
        }
      }
      CommandUtils.printErrorAndExit("Profile not found: " + profileName);
      return null;

    } catch (DataRepoClientException ex) {
      throw new IllegalArgumentException("Error processing find dataset by name");
    }
  }

  public static String getObjectName(String path) {
    String[] pathParts = StringUtils.split(path, '/');
    return pathParts[pathParts.length - 1];
  }

  public static String makeFullPath(String inPath) {
    String pwd = Context.getInstance().getContextItem(ContextEnum.PWD);

    if (inPath == null) {
      inPath = pwd;
    }

    if (StringUtils.startsWith(inPath, SLASH)) {
      return inPath;
    }

    if (StringUtils.endsWith(pwd, SLASH)) {
      return pwd + inPath;
    }
    return pwd + SLASH + inPath;
  }

  public static void printPolicyResponse(PolicyResponse policyResponse) {
    for (PolicyModel policyModel : policyResponse.getPolicies()) {
      System.out.println("Policy " + policyModel.getName());
      for (String member : policyModel.getMembers()) {
        System.out.println("  " + member);
      }
      System.out.println();
    }
  }

  /** Public Enum for the format flags used by CLI commands. */
  public enum CLIFormatFlags {
    CLI_FORMAT_TEXT("text"),
    CLI_FORMAT_JSON("json");

    private String value;

    CLIFormatFlags(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    public static CLIFormatFlags lookup(String format) {
      if (StringUtils.isEmpty(format)) {
        return CLI_FORMAT_TEXT;
      }
      for (CLIFormatFlags flag : values()) {
        if (StringUtils.equalsIgnoreCase(format, flag.getValue())) {
          return flag;
        }
      }
      printErrorAndExit("Invalid format; only text and json are supported");
      return CLI_FORMAT_TEXT; // just to make findbugs happy
    }
  }

  /**
   * Checks for valid format values (text and json are supported currently). Sets the default value
   * to text if the format is null. Prints an error message to stdout and terminates the process if
   * format is invalid.
   *
   * @param format typically the argument passed in on the command line
   * @return the validated format value, which will only be changed if the default value is used
   */
  public static String validateFormat(String format) {
    if (format == null) {
      // set the default value to text
      format = CLIFormatFlags.CLI_FORMAT_TEXT.getValue();
    } else {
      if (!StringUtils.equalsIgnoreCase(format, CLIFormatFlags.CLI_FORMAT_TEXT.getValue())
          && !StringUtils.equalsIgnoreCase(format, CLIFormatFlags.CLI_FORMAT_JSON.getValue())) {
        CommandUtils.printErrorAndExit("Invalid format; only text and json are supported");
      }
    }
    return format;
  }

  /**
   * Print the object to stdout using the Jackson object mapper default pretty printer. Prints an
   * error message to stdout and terminates the process if it encounters a JSON exception.
   *
   * @param val the object to print
   */
  public static void outputPrettyJson(Object val) {
    try {
      String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(val);
      System.out.println(json);
    } catch (JsonProcessingException ex) {
      CommandUtils.printErrorAndExit("Conversion to JSON string failed: " + ex.getMessage());
    }
  }

  public static <T> T makeRequestFromJson(String parameter, Class<T> tClass) {
    try {
      if (StringUtils.startsWith(parameter, AT)) {
        File file = new File(StringUtils.removeStart(parameter, AT));
        return CommandUtils.getObjectMapper().readValue(file, tClass);
      }

      return CommandUtils.getObjectMapper().readValue(parameter, tClass);
    } catch (IOException ex) {
      System.out.println("Error parsing json `" + parameter + "`");
      CommandUtils.printErrorAndExit(ex.getMessage());
    }
    return null; // unreachable
  }
}

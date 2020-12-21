package bio.terra.command;

import bio.terra.datarepo.model.BillingProfileModel;
import bio.terra.datarepo.model.BulkLoadArrayRequestModel;
import bio.terra.datarepo.model.BulkLoadArrayResultModel;
import bio.terra.datarepo.model.BulkLoadFileModel;
import bio.terra.datarepo.model.BulkLoadFileResultModel;
import bio.terra.datarepo.model.DatasetRequestModel;
import bio.terra.datarepo.model.DatasetSummaryModel;
import bio.terra.datarepo.model.DeleteResponseModel;
import bio.terra.datarepo.model.FileModel;
import bio.terra.datarepo.model.IngestRequestModel;
import bio.terra.datarepo.model.IngestResponseModel;
import bio.terra.datarepo.model.PolicyResponse;
import bio.terra.model.DRCollectionType;
import bio.terra.model.DRDataset;
import bio.terra.model.DRFile;
import bio.terra.parser.Argument;
import bio.terra.parser.Command;
import bio.terra.parser.Option;
import bio.terra.parser.ParsedResult;
import bio.terra.parser.Syntax;
import bio.terra.tdrwrapper.exception.DataRepoClientException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public final class DatasetCommands {

  private DatasetCommands() {}

  public static Syntax getSyntax() {
    return new Syntax()
        .addCommand(
            new Command()
                .primaryNames(new String[] {"dataset", "create"})
                .commandId(CommandEnum.COMMAND_DATASET_CREATE.getCommandId())
                .help("Create a new dataset")
                .addOption(
                    new Option()
                        .shortName("i")
                        .longName("input-json")
                        .hasArgument(true)
                        .optional(false)
                        .help("Path to a file containing the JSON form of a dataset"))
                .addOption(
                    new Option()
                        .shortName("n")
                        .longName("name")
                        .hasArgument(true)
                        .optional(true)
                        .help("Dataset name; if present, overrides the name in the JSON file"))
                .addOption(
                    new Option()
                        .shortName("p")
                        .longName("profile")
                        .hasArgument(true)
                        .optional(true)
                        .help(
                            "Profile name; if present, overrides the profile id in the JSON file")))
        .addCommand(
            new Command()
                .primaryNames(new String[] {"dataset", "show"})
                .commandId(CommandEnum.COMMAND_DATASET_SHOW.getCommandId())
                .help("List one dataset")
                .addArgument(
                    new Argument()
                        .name("dataset-name")
                        .optional(false)
                        .help("name of the dataset to show"))
                .addOption(CommandUtils.formatOption))
        .addCommand(
            new Command()
                .primaryNames(new String[] {"dataset", "delete"})
                .commandId(CommandEnum.COMMAND_DATASET_DELETE.getCommandId())
                .help("Delete a dataset")
                .addArgument(
                    new Argument()
                        .name("dataset-name")
                        .optional(false)
                        .help("Name of the dataset to delete")))
        .addCommand(
            new Command()
                .primaryNames(new String[] {"dataset", "table", "load"})
                .commandId(CommandEnum.COMMAND_DATASET_TABLE.getCommandId())
                .help("Load rows into a table")
                .addArgument(
                    new Argument().name("dataset-name").optional(false).help("Name of the dataset"))
                .addOption(
                    new Option()
                        .shortName("t")
                        .longName("table")
                        .hasArgument(true)
                        .optional(false)
                        .help("Table to load the data into"))
                .addOption(
                    new Option()
                        .shortName("i")
                        .longName("input-gspath")
                        .hasArgument(true)
                        .optional(false)
                        .help("GCS URI to the source input file"))
                .addOption(
                    new Option()
                        .longName("input-format")
                        .hasArgument(true)
                        .optional(true)
                        .help("Input format: csv or json; defaults to json")))
        // TODO: support the other parameters of a table load
        .addCommand(
            new Command()
                .primaryNames(new String[] {"dataset", "file", "load"})
                .commandId(CommandEnum.COMMAND_DATASET_FILE.getCommandId())
                .help("Load one file into a dataset")
                .addArgument(
                    new Argument()
                        .name("dataset-name")
                        .optional(false)
                        .help("Name of the dataset where the file should go"))
                .addOption(
                    new Option()
                        .shortName("p")
                        .longName("profile")
                        .hasArgument(true)
                        .optional(true)
                        .help(
                            "Identifies the profile to use for allocating storage for the file."
                                + " Defaults to the dataset profile, if not specified."))
                .addOption(
                    new Option()
                        .shortName("i")
                        .longName("input-gspath")
                        .hasArgument(true)
                        .optional(false)
                        .help("GCS URI to the source input file"))
                .addOption(
                    new Option()
                        .shortName("t")
                        .longName("target-path")
                        .hasArgument(true)
                        .optional(true)
                        .help(
                            "Target file system path in the dataset. "
                                + "If not present, the path is derived from the input gspath"))
                .addOption(
                    new Option()
                        .shortName("m")
                        .longName("mime-type")
                        .hasArgument(true)
                        .optional(true)
                        .help("Mime type of the file"))
                .addOption(
                    new Option()
                        .shortName("d")
                        .longName("description")
                        .hasArgument(true)
                        .optional(true)
                        .help("Description of the file being copied"))
                .addOption(CommandUtils.formatOption))
        .addCommand(
            new Command()
                .primaryNames(new String[] {"dataset", "file", "show"})
                .commandId(CommandEnum.COMMAND_DATASET_FILE_SHOW.getCommandId())
                .help("List one file or directory in a dataset")
                .addArgument(
                    new Argument()
                        .name("dataset-name")
                        .optional(false)
                        .help("Name of the dataset that contains the file or directory"))
                .addOption(
                    new Option()
                        .shortName("f")
                        .longName("file-path")
                        .hasArgument(true)
                        .optional(false)
                        .help("Full path to a file or directory."))
                .addOption(
                    new Option()
                        .shortName("e")
                        .longName("depth")
                        .hasArgument(true)
                        .optional(true)
                        .help(
                            "Enumeration depth. -1 means fully expand; 0 means no expansion;"
                                + "1…N expands that many subdirectories. Default value is 0."))
                .addOption(CommandUtils.formatOption))
        .addCommand(
            new Command()
                .primaryNames(new String[] {"dataset", "policy", "show"})
                .commandId(CommandEnum.COMMAND_DATASET_POLICY_SHOW.getCommandId())
                .help("Show policies")
                .addArgument(
                    new Argument()
                        .name("dataset-name")
                        .optional(false)
                        .help("Name of the dataset")))
        .addCommand(
            new Command()
                .primaryNames(new String[] {"dataset", "policy", "add"})
                .commandId(CommandEnum.COMMAND_DATASET_POLICY_ADD.getCommandId())
                .help("Add a member to a policy")
                .addArgument(
                    new Argument().name("dataset-name").optional(false).help("Name of the dataset"))
                .addOption(
                    new Option()
                        .shortName("p")
                        .longName("policy")
                        .hasArgument(true)
                        .optional(false)
                        .help("The policy to add member to"))
                .addOption(
                    new Option()
                        .shortName("e")
                        .longName("email")
                        .hasArgument(true)
                        .optional(false)
                        .help("Email of the member to be added")))
        .addCommand(
            new Command()
                .primaryNames(new String[] {"dataset", "policy", "remove"})
                .commandId(CommandEnum.COMMAND_DATASET_POLICY_REMOVE.getCommandId())
                .help("Remove a member from a policy")
                .addArgument(
                    new Argument().name("dataset-name").optional(false).help("Name of the dataset"))
                .addOption(
                    new Option()
                        .shortName("p")
                        .longName("policy")
                        .hasArgument(true)
                        .optional(false)
                        .help("The policy to remove member from"))
                .addOption(
                    new Option()
                        .shortName("e")
                        .longName("email")
                        .hasArgument(true)
                        .optional(false)
                        .help("Email of the member to be removed")));
  }

  public static boolean dispatchCommand(CommandEnum command, ParsedResult result) {
    switch (command) {
      case COMMAND_DATASET_CREATE:
        datasetCreate(
            result.getArgument("input-json"),
            result.getArgument("name"),
            result.getArgument("profile"));
        break;
      case COMMAND_DATASET_SHOW:
        datasetShow(result.getArgument("dataset-name"), result.getArgument("format"));
        break;
      case COMMAND_DATASET_DELETE:
        datasetDelete(result.getArgument("dataset-name"));
        break;
      case COMMAND_DATASET_FILE:
        datasetFileLoad(
            result.getArgument("dataset-name"),
            result.getArgument("profile-id"),
            result.getArgument("input-gspath"),
            result.getArgument("target-path"),
            result.getArgument("mime-type"),
            result.getArgument("description"),
            result.getArgument("format"));
        break;
      case COMMAND_DATASET_FILE_SHOW:
        datasetFileShow(
            result.getArgument("dataset-name"),
            result.getArgument("file-path"),
            result.getArgument("depth"),
            result.getArgument("format"));
        break;
      case COMMAND_DATASET_TABLE:
        datasetTableLoad(
            result.getArgument("dataset-name"),
            result.getArgument("input-gspath"),
            result.getArgument("table"),
            result.getArgument("input-format"));
        break;
      case COMMAND_DATASET_POLICY_ADD:
        datasetPolicyAdd(
            result.getArgument("dataset-name"),
            result.getArgument("policy"),
            result.getArgument("email"));
        break;
      case COMMAND_DATASET_POLICY_REMOVE:
        datasetPolicyRemove(
            result.getArgument("dataset-name"),
            result.getArgument("policy"),
            result.getArgument("email"));
        break;
      case COMMAND_DATASET_POLICY_SHOW:
        datasetPolicyShow(result.getArgument("dataset-name"));
        break;
      default:
        return false;
    }
    return true;
  }

  private static void datasetCreate(String jsonpath, String name, String profileName) {
    try {
      File file = new File(jsonpath);
      DatasetRequestModel datasetRequestModel =
          CommandUtils.getObjectMapper().readValue(file, DatasetRequestModel.class);
      if (datasetRequestModel != null) {
        // Override the name and profile if requested
        if (name != null) {
          datasetRequestModel.name(name);
        }
        if (profileName != null) {
          BillingProfileModel profileModel = CommandUtils.findProfileByName(profileName);
          datasetRequestModel.defaultProfileId(profileModel.getId());
        }

        DatasetSummaryModel datasetSummary = DRApi.get().createDataset(datasetRequestModel);

        System.out.println(datasetSummary.toString());
      }
    } catch (IOException ex) {
      System.out.println("Error parsing file " + jsonpath + ":");
      System.out.println(ex.getMessage());
    } catch (DataRepoClientException ex) {
      System.out.println("Error processing dataset create:");
      CommandUtils.printError(ex);
    }
  }

  private static void datasetDelete(String datasetName) {
    DatasetSummaryModel summary = CommandUtils.findDatasetByName(datasetName);

    try {
      DeleteResponseModel deleteResponse = DRApi.get().deleteDataset(summary.getId());

      System.out.printf(
          "Dataset deleted: %s (%s)%n", datasetName, deleteResponse.getObjectState().getValue());
    } catch (DataRepoClientException ex) {
      System.out.println("Error processing dataset delete:");
      CommandUtils.printError(ex);
    }
  }

  private static void datasetShow(String datasetName, String format) {
    format = CommandUtils.validateFormat(format);

    // Show dataset is the same as describe
    DatasetSummaryModel summary = CommandUtils.findDatasetByName(datasetName);
    DRDataset datasetElement = new DRDataset(summary);
    datasetElement.describe(format);
  }

  private static void datasetPolicyShow(String datasetName) {
    DatasetSummaryModel summary = CommandUtils.findDatasetByName(datasetName);
    try {
      PolicyResponse policyResponse = DRApi.get().retrieveDatasetPolicies(summary.getId());
      CommandUtils.printPolicyResponse(policyResponse);
    } catch (DataRepoClientException ex) {
      System.out.println("Error processing show policy:");
      CommandUtils.printError(ex);
    }
  }

  private static void datasetPolicyAdd(String datasetName, String policyName, String email) {
    DatasetSummaryModel summary = CommandUtils.findDatasetByName(datasetName);
    try {
      PolicyResponse policyResponse =
          DRApi.get().addDatasetPolicyMember(summary.getId(), policyName, email);
      CommandUtils.printPolicyResponse(policyResponse);
    } catch (DataRepoClientException ex) {
      System.out.println("Error adding policy member:");
      CommandUtils.printError(ex);
    }
  }

  private static void datasetPolicyRemove(String datasetName, String policyName, String email) {
    DatasetSummaryModel summary = CommandUtils.findDatasetByName(datasetName);
    try {
      PolicyResponse policyResponse =
          DRApi.get().deleteDatasetPolicyMember(summary.getId(), policyName, email);
      CommandUtils.printPolicyResponse(policyResponse);
    } catch (DataRepoClientException ex) {
      System.out.println("Error removing policy member:");
      CommandUtils.printError(ex);
    }
  }

  private static IngestRequestModel.FormatEnum lookupFormatEnum(String inputFormat) {
    if (inputFormat != null) {
      IngestRequestModel.FormatEnum formatEnum =
          IngestRequestModel.FormatEnum.fromValue(inputFormat);
      if (formatEnum != null) {
        return formatEnum;
      }
    }
    return IngestRequestModel.FormatEnum.JSON;
  }

  private static void datasetTableLoad(
      String datasetName, String inputGspath, String tableName, String inputFormat) {
    DatasetSummaryModel summary = CommandUtils.findDatasetByName(datasetName);

    IngestRequestModel ingestRequest =
        new IngestRequestModel()
            .table(tableName)
            .path(inputGspath)
            .format(lookupFormatEnum(inputFormat));

    try {
      IngestResponseModel response = DRApi.get().ingestDataset(summary.getId(), ingestRequest);
      System.out.printf(
          "Loaded %d rows; %d bad rows skipped%n",
          response.getRowCount(), response.getBadRowCount());

    } catch (DataRepoClientException ex) {
      System.out.println("Error processing table load: ");
      CommandUtils.printError(ex);
    }
  }

  private static void datasetFileLoad(
      String datasetName,
      String profileName,
      String inputGspath,
      String targetPath,
      String mimeType,
      String description,
      String format) {
    format = CommandUtils.validateFormat(format);

    DatasetSummaryModel summary = CommandUtils.findDatasetByName(datasetName);
    String datasetId = summary.getId();

    try {
      // If no profile name is specified, use the default profile from the dataset.
      // Otherwise, lookup the profile name and use that id.
      String profileId;
      if (profileName == null) {
        profileId = summary.getDefaultProfileId();
      } else {
        BillingProfileModel profileModel = CommandUtils.findProfileByName(profileName);
        profileId = profileModel.getId();
      }

      if (targetPath == null) {
        String[] pathParts = StringUtils.split(inputGspath, '/');
        if (pathParts.length < 3) {
          CommandUtils.printErrorAndExit("Invalid GS URI");
        }
        String encodedPath = '/' + StringUtils.join(pathParts, '/', 2, pathParts.length);
        targetPath = URLDecoder.decode(encodedPath, "UTF-8");
      }

      if (mimeType == null) {
        mimeType = StringUtils.EMPTY;
      }

      if (description == null) {
        description = StringUtils.EMPTY;
      }

      BulkLoadFileModel loadModel =
          new BulkLoadFileModel()
              .sourcePath(inputGspath)
              .targetPath(targetPath)
              .mimeType(mimeType)
              .description(description);

      BulkLoadArrayRequestModel loadRequest =
          new BulkLoadArrayRequestModel().profileId(profileId).addLoadArrayItem(loadModel);

      BulkLoadArrayResultModel resultModel = DRApi.get().bulkFileLoadArray(datasetId, loadRequest);

      BulkLoadFileResultModel fileResultModel = resultModel.getLoadFileResults().get(0);
      switch (fileResultModel.getState()) {
        case SUCCEEDED:
          break;

        case FAILED:
          CommandUtils.printErrorAndExit("File load failed: " + fileResultModel.getError());
          break;

        case RUNNING:
        case NOT_TRIED:
          CommandUtils.printErrorAndExit(
              "File load in a weird state: " + fileResultModel.getState());
          break;
      }

      FileModel fileModel = DRApi.get().lookupFileById(datasetId, fileResultModel.getFileId(), 0);
      DRFile drFile = new DRFile(DRCollectionType.COLLECTION_TYPE_DATASET, fileModel);
      drFile.describe(format);
    } catch (DataRepoClientException ex) {
      System.out.println("Error processing file ingest: ");
      CommandUtils.printError(ex);
    } catch (UnsupportedEncodingException e) {
      CommandUtils.printErrorAndExit("Error decoding gspath into target URI:\n" + e.getMessage());
    }
  }

  private static void datasetFileShow(
      String datasetName, String filePath, String depth, String format) {
    format = CommandUtils.validateFormat(format);

    Integer depthInt = 0; // default depth value is 0
    if (depth != null) {
      try {
        depthInt = Integer.valueOf(depth);
      } catch (NumberFormatException nfEx) {
        CommandUtils.printErrorAndExit("Invalid depth; only integer values are supported");
      }
      if (depthInt < -1) {
        CommandUtils.printErrorAndExit("Invalid depth; only integer values >= -1 are supported");
      }
    }

    // Show dataset file is the same as describe
    DatasetSummaryModel datasetSummary = CommandUtils.findDatasetByName(datasetName);
    FileModel fileModel = null;
    try {
      fileModel = DRApi.get().lookupFileByPath(datasetSummary.getId(), filePath, depthInt);
    } catch (DataRepoClientException ex) {
      CommandUtils.printErrorAndExit("Error processing file lookup: " + ex.getMessage());
    }

    DRFile drFile = new DRFile(DRCollectionType.COLLECTION_TYPE_DATASET, fileModel);
    drFile.describe(format);
  }
}

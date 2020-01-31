package bio.terra.command;

import bio.terra.datarepo.api.RepositoryApi;
import bio.terra.datarepo.client.ApiException;
import bio.terra.datarepo.model.BillingProfileModel;
import bio.terra.datarepo.model.DatasetRequestModel;
import bio.terra.datarepo.model.DatasetSummaryModel;
import bio.terra.datarepo.model.DeleteResponseModel;
import bio.terra.datarepo.model.FileLoadModel;
import bio.terra.datarepo.model.FileModel;
import bio.terra.datarepo.model.IngestRequestModel;
import bio.terra.datarepo.model.IngestResponseModel;
import bio.terra.datarepo.model.JobModel;
import bio.terra.datarepo.model.PolicyMemberRequest;
import bio.terra.datarepo.model.PolicyResponse;
import bio.terra.model.DRCollectionType;
import bio.terra.model.DRDataset;
import bio.terra.model.DRFile;
import bio.terra.parser.Argument;
import bio.terra.parser.Command;
import bio.terra.parser.Option;
import bio.terra.parser.ParsedResult;
import bio.terra.parser.Syntax;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public final class DatasetCommands {

    private DatasetCommands() { }

    public static Syntax getSyntax() {
        return new Syntax()
                .addCommand(new Command()
                        .primaryNames(new String[]{"dataset", "create"})
                        .commandId(CommandEnum.COMMAND_DATASET_CREATE.getCommandId())
                        .help("Create a new dataset")
                        .addOption(new Option()
                                .shortName("i")
                                .longName("input-json")
                                .hasArgument(true)
                                .optional(false)
                                .help("Path to a file containing the JSON form of a dataset"))
                        .addOption(new Option()
                                .shortName("n")
                                .longName("name")
                                .hasArgument(true)
                                .optional(true)
                                .help("Dataset name; if present, overrides the name in the JSON file"))
                        .addOption(new Option()
                                .shortName("p")
                                .longName("profile")
                                .hasArgument(true)
                                .optional(true)
                                .help("Profile name; if present, overrides the profile id in the JSON file")))
                .addCommand(new Command()
                        .primaryNames(new String[]{"dataset", "show"})
                        .commandId(CommandEnum.COMMAND_DATASET_SHOW.getCommandId())
                        .help("List one dataset")
                        .addArgument(new Argument()
                                .name("dataset-name")
                                .optional(false)
                                .help("name of the dataset to show")))
                .addCommand(new Command()
                        .primaryNames(new String[]{"dataset", "delete"})
                        .commandId(CommandEnum.COMMAND_DATASET_DELETE.getCommandId())
                        .help("Delete a dataset")
                        .addArgument(new Argument()
                                .name("dataset-name")
                                .optional(false)
                                .help("Name of the dataset to delete")))
                .addCommand(new Command()
                        .primaryNames(new String[]{"dataset", "table", "load"})
                        .commandId(CommandEnum.COMMAND_DATASET_TABLE.getCommandId())
                        .help("Load rows into a table")
                        .addArgument(new Argument()
                                .name("dataset-name")
                                .optional(false)
                                .help("Name of the dataset"))
                        .addOption(new Option()
                                .shortName("t")
                                .longName("table")
                                .hasArgument(true)
                                .optional(false)
                                .help("Table to load the data into"))
                        .addOption(new Option()
                                .shortName("i")
                                .longName("input-gspath")
                                .hasArgument(true)
                                .optional(false)
                                .help("GCS URI to the source input file"))
                        .addOption(new Option()
                                .shortName("s")
                                .longName("strategy")
                                .hasArgument(true)
                                .optional(true)
                                .help("Load strategy: append or upsert; defaults to append"))
                        .addOption(new Option()
                                .longName("input-format")
                                .hasArgument(true)
                                .optional(true)
                                .help("Input format: csv or json; defaults to json")))
                        // TODO: support the other parameters of a table load
                .addCommand(new Command()
                        .primaryNames(new String[]{"dataset", "file", "load"})
                        .commandId(CommandEnum.COMMAND_DATASET_FILE.getCommandId())
                        .help("Load one file into a dataset")
                        .addArgument(new Argument()
                                .name("dataset-name")
                                .optional(false)
                                .help("Name of the dataset where the file should go"))
                        .addOption(new Option()
                                .shortName("p")
                                .longName("profile")
                                .hasArgument(true)
                                .optional(true)
                                .help("Identifies the profile to use for allocating storage for the file." +
                                        " Defaults to the dataset profile, if not specified."))
                        .addOption(new Option()
                                .shortName("i")
                                .longName("input-gspath")
                                .hasArgument(true)
                                .optional(false)
                                .help("GCS URI to the source input file"))
                        .addOption(new Option()
                                .shortName("t")
                                .longName("target-path")
                                .hasArgument(true)
                                .optional(true)
                                .help("Target file system path in the dataset. " +
                                        "If not present, the path is derived from the input gspath"))
                        .addOption(new Option()
                                .shortName("m")
                                .longName("mime-type")
                                .hasArgument(true)
                                .optional(true)
                                .help("Mime type of the file"))
                        .addOption(new Option()
                                .shortName("d")
                                .longName("description")
                                .hasArgument(true)
                                .optional(true)
                                .help("Description of the file being copied"))
                        .addOption(new Option()
                                .longName("format")
                                .hasArgument(true)
                                .optional(true)
                                .help("Choose format; 'text' is the default; 'json' is supported")))
                .addCommand(new Command()
                        .primaryNames(new String[]{"dataset", "file", "lookup"})
                        .commandId(CommandEnum.COMMAND_DATASET_FILE_LOOKUP.getCommandId())
                        .help("Lookup metadata for one file or directory in a dataset")
                        .addArgument(new Argument()
                                .name("dataset-name")
                                .optional(false)
                                .help("Name of the dataset that contains the file or directory"))
                        .addOption(new Option()
                                .shortName("f")
                                .longName("file-path")
                                .hasArgument(true)
                                .optional(false)
                                .help("The full path to a file or directory."))
                        .addOption(new Option()
                                .shortName("e")
                                .longName("depth")
                                .hasArgument(true)
                                .optional(true)
                                .help("Enumeration depth. -1 means fully expand; 0 means no expansion;" +
                                        "1…N expands that many subdirectories"))
                        .addOption(new Option()
                                .longName("format")
                                .hasArgument(true)
                                .optional(true)
                                .help("Choose format; 'text' is the default; 'json' is supported")))
                .addCommand(new Command()
                        .primaryNames(new String[]{"dataset", "policy", "show"})
                        .commandId(CommandEnum.COMMAND_DATASET_POLICY_SHOW.getCommandId())
                        .help("Show policies")
                        .addArgument(new Argument()
                                .name("dataset-name")
                                .optional(false)
                                .help("Name of the dataset")))
                .addCommand(new Command()
                        .primaryNames(new String[]{"dataset", "policy", "add"})
                        .commandId(CommandEnum.COMMAND_DATASET_POLICY_ADD.getCommandId())
                        .help("Add a member to a policy")
                        .addArgument(new Argument()
                                .name("dataset-name")
                                .optional(false)
                                .help("Name of the dataset"))
                        .addOption(new Option()
                                .shortName("p")
                                .longName("policy")
                                .hasArgument(true)
                                .optional(false)
                                .help("The policy to add member to"))
                        .addOption(new Option()
                                .shortName("e")
                                .longName("email")
                                .hasArgument(true)
                                .optional(false)
                                .help("Email of the member to be added")))
                .addCommand(new Command()
                        .primaryNames(new String[]{"dataset", "policy", "remove"})
                        .commandId(CommandEnum.COMMAND_DATASET_POLICY_REMOVE.getCommandId())
                        .help("Remove a member from a policy")
                        .addArgument(new Argument()
                                .name("dataset-name")
                                .optional(false)
                                .help("Name of the dataset"))
                        .addOption(new Option()
                                .shortName("p")
                                .longName("policy")
                                .hasArgument(true)
                                .optional(false)
                                .help("The policy to remove member from"))
                        .addOption(new Option()
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
                datasetShow(result.getArgument("dataset-name"));
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
            case COMMAND_DATASET_FILE_LOOKUP:
                datasetFileLookup(
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
                        result.getArgument("strategy"),
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
            DatasetRequestModel datasetRequestModel = CommandUtils.getObjectMapper()
                    .readValue(file, DatasetRequestModel.class);
            if (datasetRequestModel != null) {
                // Override the name and profile if requested
                if (name != null) {
                    datasetRequestModel.name(name);
                }
                if (profileName != null) {
                    BillingProfileModel profileModel = CommandUtils.findProfileByName(profileName);
                    datasetRequestModel.defaultProfileId(profileModel.getId());
                }

                DatasetSummaryModel datasetSummary = DRApis.getRepositoryApi().createDataset(datasetRequestModel);
                System.out.println(datasetSummary.toString());
            }
        } catch (IOException ex) {
            System.out.println("Error parsing file " + jsonpath + ":");
            System.out.println(ex.getMessage());
        } catch (ApiException ex) {
            System.out.println("Error processing dataset create:");
            CommandUtils.printError(ex);
        }
    }

    private static void datasetDelete(String datasetName) {
        DatasetSummaryModel summary = CommandUtils.findDatasetByName(datasetName);

        try {
            DeleteResponseModel deleteResponse = DRApis.getRepositoryApi().deleteDataset(summary.getId());
            System.out.printf("Dataset deleted: %s (%s)%n", datasetName, deleteResponse.getObjectState().getValue());
        } catch (ApiException ex) {
            System.out.println("Error processing dataset delete:");
            CommandUtils.printError(ex);
        }
    }

    private static void datasetShow(String datasetName) {
        // Show dataset is the same as describe
        DatasetSummaryModel summary = CommandUtils.findDatasetByName(datasetName);
        DRDataset datasetElement = new DRDataset(summary);
        datasetElement.describe();
    }

    private static void datasetPolicyShow(String datasetName) {
        DatasetSummaryModel summary = CommandUtils.findDatasetByName(datasetName);
        try {
            PolicyResponse policyResponse = DRApis.getRepositoryApi().retrieveDatasetPolicies(summary.getId());
            CommandUtils.printPolicyResponse(policyResponse);
        } catch (ApiException ex) {
            System.out.println("Error processing show policy:");
            CommandUtils.printError(ex);
        }
    }

    private static void datasetPolicyAdd(String datasetName, String policyName, String email) {
        PolicyMemberRequest member = new PolicyMemberRequest().email(email);
        DatasetSummaryModel summary = CommandUtils.findDatasetByName(datasetName);
        try {
            PolicyResponse policyResponse = DRApis.getRepositoryApi()
                    .addDatasetPolicyMember(summary.getId(), policyName, member);
            CommandUtils.printPolicyResponse(policyResponse);
        } catch (ApiException ex) {
            System.out.println("Error adding policy member:");
            CommandUtils.printError(ex);
        }
    }

    private static void datasetPolicyRemove(String datasetName, String policyName, String email) {
        DatasetSummaryModel summary = CommandUtils.findDatasetByName(datasetName);
        try {
            PolicyResponse policyResponse = DRApis.getRepositoryApi()
                    .deleteDatasetPolicyMember(summary.getId(), policyName, email);
            CommandUtils.printPolicyResponse(policyResponse);
        } catch (ApiException ex) {
            System.out.println("Error removing policy member:");
            CommandUtils.printError(ex);
        }
    }

    private static IngestRequestModel.FormatEnum lookupFormatEnum(String inputFormat) {
        if (inputFormat != null) {
            IngestRequestModel.FormatEnum formatEnum = IngestRequestModel.FormatEnum.fromValue(inputFormat);
            if (formatEnum != null) {
                return formatEnum;
            }
        }
        return IngestRequestModel.FormatEnum.JSON;
    }

    private static IngestRequestModel.StrategyEnum lookupStrategyEnum(String strategy) {
        if (strategy != null) {
            IngestRequestModel.StrategyEnum strategyEnum = IngestRequestModel.StrategyEnum.fromValue(strategy);
            if (strategyEnum != null) {
                return strategyEnum;
            }
        }
        return IngestRequestModel.StrategyEnum.APPEND;
    }

    private static void datasetTableLoad(String datasetName,
                                         String inputGspath,
                                         String tableName,
                                         String strategy,
                                         String inputFormat) {
        DatasetSummaryModel summary = CommandUtils.findDatasetByName(datasetName);

        IngestRequestModel ingestRequest = new IngestRequestModel()
                .table(tableName)
                .path(inputGspath)
                .format(lookupFormatEnum(inputFormat))
                .strategy(lookupStrategyEnum(strategy));

        try {
            JobModel job = DRApis.getRepositoryApi().ingestDataset(summary.getId(), ingestRequest);
            IngestResponseModel response = CommandUtils.waitForResponse(
                    DRApis.getRepositoryApi(),
                    job,
                    1,
                    IngestResponseModel.class);

            System.out.printf("Loaded %d rows; %d bad rows skipped%n",
                    response.getRowCount(),
                    response.getBadRowCount());

        } catch (ApiException ex) {
            System.out.println("Error processing table load: ");
            CommandUtils.printError(ex);
        }

    }

    private static void datasetFileLoad(String datasetName,
                                       String profileName,
                                       String inputGspath,
                                       String targetPath,
                                       String mimeType,
                                       String description,
                                       String format) {
        if (format == null) {
            format = "text";
        } else {
            if (!StringUtils.equalsIgnoreCase(format, "text") && !StringUtils.equalsIgnoreCase(format, "json")) {
                CommandUtils.printErrorAndExit("Invalid format; only text and json are supported");
            }
        }

        DatasetSummaryModel summary = CommandUtils.findDatasetByName(datasetName);

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

            FileLoadModel loadModel = new FileLoadModel()
                    .profileId(profileId)
                    .sourcePath(inputGspath)
                    .targetPath(targetPath)
                    .mimeType(mimeType)
                    .description(description);

            RepositoryApi api = DRApis.getRepositoryApi();
            JobModel jobModel = api.ingestFile(summary.getId(), loadModel);
            FileModel fileModel = CommandUtils.waitForResponse(
                        api,
                        jobModel,
                        1,
                        FileModel.class);

            if (StringUtils.equalsIgnoreCase(format, "text")) {
                DRFile drFile = new DRFile(DRCollectionType.COLLECTION_TYPE_DATASET, fileModel);
                drFile.describe();
            } else {
                // json format
                String json = CommandUtils.getObjectMapper().writeValueAsString(fileModel);
                System.out.println(json);
            }

        } catch (JsonProcessingException ex) {
            CommandUtils.printErrorAndExit("Conversion to JSON string failed: " + ex.getMessage());
        } catch (ApiException ex) {
            System.out.println("Error processing file ingest: ");
            CommandUtils.printError(ex);
        } catch (UnsupportedEncodingException e) {
            CommandUtils.printErrorAndExit("Error decoding gspath into target URI:\n" + e.getMessage());
        }
    }

    private static void datasetFileLookup(String datasetName,
                                        String filePath,
                                        String depth,
                                        String format) {
        Integer depthInt = 0;
        if (depth == null) {
            depthInt = -1;
        } else {
            try {
                depthInt = Integer.valueOf(depth);
            } catch (NumberFormatException nfEx) {
                CommandUtils.printErrorAndExit("Invalid depth; only integer values are supported");
            }
        }

        if (format == null) {
            format = "text";
        } else {
            if (!StringUtils.equalsIgnoreCase(format, "text") && !StringUtils.equalsIgnoreCase(format, "json")) {
                CommandUtils.printErrorAndExit("Invalid format; only text and json are supported");
            }
        }

        DatasetSummaryModel datasetSummary = CommandUtils.findDatasetByName(datasetName);

        try {
            RepositoryApi api = DRApis.getRepositoryApi();
            FileModel fileModel = api.lookupFileByPath(datasetSummary.getId(), filePath, depthInt);

            if (StringUtils.equalsIgnoreCase(format, "text")) {
                DRFile drFile = new DRFile(DRCollectionType.COLLECTION_TYPE_DATASET, fileModel);
                drFile.describe();
            } else {
                // json format
                String json = CommandUtils.getObjectMapper().writeValueAsString(fileModel);
                System.out.println(json);
            }

        } catch (JsonProcessingException ex) {
            CommandUtils.printErrorAndExit("Conversion to JSON string failed: " + ex.getMessage());
        } catch (ApiException ex) {
            System.out.println("Error processing file lookup: ");
            CommandUtils.printError(ex);
        }
    }
}

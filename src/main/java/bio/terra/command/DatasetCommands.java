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
import bio.terra.parser.Syntax;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class DatasetCommands {

    public static Syntax getSyntax() {
        return new Syntax()
                .addCommand(new Command()
                        .primaryName("dataset")
                        .secondaryName("create")
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
                        .primaryName("dataset")
                        .secondaryName("show")
                        .commandId(CommandEnum.COMMAND_DATASET_SHOW.getCommandId())
                        .help("List one dataset")
                        .addArgument(new Argument()
                                .name("dataset-name")
                                .optional(false)
                                .help("name of the dataset to show")))
                .addCommand(new Command()
                        .primaryName("dataset")
                        .secondaryName("delete")
                        .commandId(CommandEnum.COMMAND_DATASET_DELETE.getCommandId())
                        .help("Delete a dataset")
                        .addArgument(new Argument()
                                .name("dataset-name")
                                .optional(false)
                                .help("Name of the dataset to delete")))
                .addCommand(new Command()
                        .primaryName("dataset")
                        .secondaryName("table-load")
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
                                .help("GCS URI to the source input file")))
                        // TODO: support the other parameters of a table load
                .addCommand(new Command()
                        .primaryName("dataset")
                        .secondaryName("file-load")
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
                        .primaryName("dataset")
                        .secondaryName("policy-show")
                        .commandId(CommandEnum.COMMAND_DATASET_POLICY_SHOW.getCommandId())
                        .help("Show policies")
                        .addArgument(new Argument()
                                .name("dataset-name")
                                .optional(false)
                                .help("Name of the dataset")))
                .addCommand(new Command()
                        .primaryName("dataset")
                        .secondaryName("policy-add")
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
                        .primaryName("dataset")
                        .secondaryName("policy-remove")
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

    public static void datasetCreate(String jsonpath, String name, String profileName) {
        try {
            File file = new File(jsonpath);
            DatasetRequestModel datasetRequestModel = CommandUtils.getObjectMapper().readValue(file, DatasetRequestModel.class);
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

    public static void datasetDelete(String datasetName) {
        DatasetSummaryModel summary = CommandUtils.findDatasetByName(datasetName);

        try {
            DeleteResponseModel deleteResponse = DRApis.getRepositoryApi().deleteDataset(summary.getId());
            System.out.printf("Dataset deleted: %s (%s)\n", datasetName, deleteResponse.getObjectState().getValue());
        } catch (ApiException ex) {
            System.out.println("Error processing dataset delete:");
            CommandUtils.printError(ex);
        }
    }

    public static void datasetShow(String datasetName) {
        // Show dataset is the same as describe
        DatasetSummaryModel summary = CommandUtils.findDatasetByName(datasetName);
        DRDataset datasetElement = new DRDataset(summary);
        datasetElement.describe();
    }

    public static void datasetPolicyShow(String datasetName) {
        DatasetSummaryModel summary = CommandUtils.findDatasetByName(datasetName);
        try {
            PolicyResponse policyResponse = DRApis.getRepositoryApi().retrieveDatasetPolicies(summary.getId());
            CommandUtils.printPolicyResponse(policyResponse);
        } catch (ApiException ex) {
            System.out.println("Error processing show policy:");
            CommandUtils.printError(ex);
        }
    }

    public static void datasetPolicyAdd(String datasetName, String policyName, String email) {
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

    public static void datasetPolicyRemove(String datasetName, String policyName, String email) {
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

    public static void datasetTableLoad(String datasetName,
                                        String inputGspath,
                                        String tableName) {
        DatasetSummaryModel summary = CommandUtils.findDatasetByName(datasetName);

        IngestRequestModel ingestRequest = new IngestRequestModel()
                .table(tableName)
                .path(inputGspath)
                .format(IngestRequestModel.FormatEnum.JSON);

        try {
            JobModel job = DRApis.getRepositoryApi().ingestDataset(summary.getId(), ingestRequest);
            IngestResponseModel response = CommandUtils.waitForResponse(
                    DRApis.getRepositoryApi(),
                    job,
                    1,
                    IngestResponseModel.class);

            System.out.printf("Loaded %d rows; %d bad rows skipped\n",
                    response.getRowCount(),
                    response.getBadRowCount());

        } catch (ApiException ex) {
            System.out.println("Error processing table load: ");
            CommandUtils.printError(ex);
        }

    }

    public static void datasetFileLoad(String datasetName,
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
}

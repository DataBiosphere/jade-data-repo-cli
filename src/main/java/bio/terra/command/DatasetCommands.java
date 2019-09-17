package bio.terra.command;

import bio.terra.datarepo.api.RepositoryApi;
import bio.terra.datarepo.client.ApiException;
import bio.terra.datarepo.model.DatasetRequestModel;
import bio.terra.datarepo.model.DatasetSummaryModel;
import bio.terra.datarepo.model.DeleteResponseModel;
import bio.terra.datarepo.model.FileLoadModel;
import bio.terra.datarepo.model.FileModel;
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
                                .help("Path to a file containing the JSON form of a dataset")))
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
                        .secondaryName("file-load")
                        .commandId(CommandEnum.COMMAND_DATASET_FILE.getCommandId())
                        .help("Load one file into a dataset")
                        .addArgument(new Argument()
                                .name("dataset-name")
                                .optional(false)
                                .help("Name of the dataset where the file should go"))
                        .addOption(new Option()
                                .shortName("p")
                                .longName("profile-id")
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
                                .help("Description of the file being copied")))
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

    public static void datasetCreate(String jsonpath) {
        try {
            File file = new File(jsonpath);
            DatasetRequestModel datasetRequestModel = CommandUtils.getObjectMapper().readValue(file, DatasetRequestModel.class);
            if (datasetRequestModel != null) {
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

    public static void datasetIngestFile(String datasetName,
                                  String profileId,
                                  String inputGspath,
                                  String targetPath,
                                  String mimeType,
                                  String description) {
        DatasetSummaryModel summary = CommandUtils.findDatasetByName(datasetName);

        try {
            if (profileId == null) {
                profileId = summary.getDefaultProfileId();
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

            DRFile drFile = new DRFile(DRCollectionType.COLLECTION_TYPE_DATASET, fileModel);
            drFile.describe();

        } catch (ApiException ex) {
            System.out.println("Error processing file ingest: ");
            CommandUtils.printError(ex);
        } catch (UnsupportedEncodingException e) {
            CommandUtils.printErrorAndExit("Error decoding gspath into target URI:\n" + e.getMessage());
        }
    }
}

package bio.terra.command;

import bio.terra.datarepo.api.RepositoryApi;
import bio.terra.datarepo.client.ApiException;
import bio.terra.datarepo.model.DatasetRequestModel;
import bio.terra.datarepo.model.DatasetSummaryModel;
import bio.terra.datarepo.model.DeleteResponseModel;
import bio.terra.datarepo.model.FileLoadModel;
import bio.terra.datarepo.model.FileModel;
import bio.terra.datarepo.model.JobModel;
import bio.terra.model.DRDataset;
import bio.terra.model.DRFile;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class DatasetCommands {
    private static DatasetCommands theDatasetCommands;

    private DatasetCommands() {
    }

    public static DatasetCommands getInstance() {
        if (theDatasetCommands == null) {
            theDatasetCommands = new DatasetCommands();
        }
        return theDatasetCommands;
    }

    public void datasetCreate(String jsonpath) {
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

    public void datasetDelete(String datasetName) {
        DatasetSummaryModel summary = CommandUtils.findDatasetByName(datasetName);

        try {
            DeleteResponseModel deleteResponse = DRApis.getRepositoryApi().deleteDataset(summary.getId());
            System.out.printf("Dataset deleted: %s (%s)", datasetName, deleteResponse.getObjectState().getValue());
        } catch (ApiException ex) {
            System.out.println("Error processing dataset delete:");
            CommandUtils.printError(ex);
        }
    }

    public void datasetShow(String datasetName) {
        // Show dataset is the same as describe
        DatasetSummaryModel summary = CommandUtils.findDatasetByName(datasetName);
        DRDataset datasetElement = new DRDataset(summary);
        datasetElement.describe();
    }

    public void datasetIngestFile(String datasetName,
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

            DRFile drFile = new DRFile(fileModel);
            drFile.describe();

        } catch (ApiException ex) {
            System.out.println("Error processing file ingest: ");
            CommandUtils.printError(ex);
        } catch (UnsupportedEncodingException e) {
            CommandUtils.printErrorAndExit("Error decoding gspath into target URI:\n" + e.getMessage());
        }
    }
}

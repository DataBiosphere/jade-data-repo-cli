package bio.terra.command;

import bio.terra.datarepo.client.ApiException;
import bio.terra.datarepo.model.DatasetRequestModel;
import bio.terra.datarepo.model.DatasetSummaryModel;
import bio.terra.datarepo.model.DeleteResponseModel;
import bio.terra.model.DRDataset;

import java.io.File;
import java.io.IOException;

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
            System.out.printf("Study deleted: %s (%s)", datasetName, deleteResponse.getObjectState().getValue());
        } catch (ApiException ex) {
            System.out.println("Error processing study delete:");
            CommandUtils.printError(ex);
        }
    }

    public void datasetShow(String datasetName) {
        // Show study is the same as describe
        DatasetSummaryModel summary = CommandUtils.findDatasetByName(datasetName);
        DRDataset studyElement = new DRDataset(summary);
        studyElement.describe();
    }
}

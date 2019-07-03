package bio.terra.command;

import bio.terra.datarepo.api.RepositoryApi;
import bio.terra.datarepo.client.ApiException;
import bio.terra.datarepo.model.DatasetRequestModel;
import bio.terra.datarepo.model.DatasetSummaryModel;
import bio.terra.datarepo.model.JobModel;

import java.io.File;
import java.io.IOException;

public class DatasetCommands {
    RepositoryApi api;

    public DatasetCommands() {
        api = new RepositoryApi();
    }

    public void datasetCreate(String jsonpath) {
        try {
            File file = new File(jsonpath);
            DatasetRequestModel datasetRequestModel = CommandUtils.getObjectMapper().readValue(file, DatasetRequestModel.class);
            if (datasetRequestModel != null) {
                JobModel job = api.createDataset(datasetRequestModel);
                DatasetSummaryModel summaryModel = CommandUtils.waitForResponse(
                        api,
                        job,
                        1,
                        DatasetSummaryModel.class);
            }
        } catch (IOException ex) {
            System.out.println("Error parsing file " + jsonpath + ":");
            System.out.println(ex.getMessage());
        } catch (ApiException ex) {
            System.out.println("Error processing dataset create:");
            CommandUtils.printError(ex);
        }
    }

}

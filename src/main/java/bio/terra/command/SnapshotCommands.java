package bio.terra.command;

import bio.terra.datarepo.api.RepositoryApi;
import bio.terra.datarepo.client.ApiException;
import bio.terra.datarepo.model.JobModel;
import bio.terra.datarepo.model.SnapshotRequestModel;
import bio.terra.datarepo.model.SnapshotSummaryModel;

import java.io.File;
import java.io.IOException;

public class SnapshotCommands {
    RepositoryApi api;

    public SnapshotCommands() {
        api = new RepositoryApi();
    }

    public void snapshotCreate(String jsonpath) {
        try {
            File file = new File(jsonpath);
            SnapshotRequestModel snapshotRequestModel = CommandUtils.getObjectMapper().readValue(file, SnapshotRequestModel.class);
            if (snapshotRequestModel != null) {
                JobModel job = api.createSnapshot(snapshotRequestModel);
                SnapshotSummaryModel summaryModel = CommandUtils.waitForResponse(
                        api,
                        job,
                        1,
                        SnapshotSummaryModel.class);
            }
        } catch (IOException ex) {
            System.out.println("Error parsing file " + jsonpath + ":");
            System.out.println(ex.getMessage());
        } catch (ApiException ex) {
            System.out.println("Error processing snapshot create:");
            CommandUtils.printError(ex);
        }
    }

}

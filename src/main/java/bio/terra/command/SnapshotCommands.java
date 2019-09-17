package bio.terra.command;

import bio.terra.datarepo.api.RepositoryApi;
import bio.terra.datarepo.client.ApiException;
import bio.terra.datarepo.model.DeleteResponseModel;
import bio.terra.datarepo.model.JobModel;
import bio.terra.datarepo.model.SnapshotRequestModel;
import bio.terra.datarepo.model.SnapshotSummaryModel;
import bio.terra.model.DRSnapshot;
import bio.terra.parser.Argument;
import bio.terra.parser.Command;
import bio.terra.parser.Option;
import bio.terra.parser.Syntax;

import java.io.File;
import java.io.IOException;

public class SnapshotCommands {
    private static final RepositoryApi api = new RepositoryApi();

    public static Syntax getSyntax() {

        return new Syntax()
                .addCommand(new Command()
                        .primaryName("snapshot")
                        .secondaryName("create")
                        .commandId(CommandEnum.COMMAND_SNAPSHOT_CREATE.getCommandId())
                        .help("Create a new snapshot")
                        .addOption(new Option()
                                .shortName("i")
                                .longName("input-json")
                                .hasArgument(true)
                                .optional(false)
                                .help("Path to a file containing the JSON form of a snapshot")))
                .addCommand(new Command()
                        .primaryName("snapshot")
                        .secondaryName("show")
                        .commandId(CommandEnum.COMMAND_SNAPSHOT_SHOW.getCommandId())
                        .help("List one snapshot")
                        .addArgument(new Argument()
                                .name("snapshot-name")
                                .optional(false)
                                .help("name of the snapshot to show")))
                .addCommand(new Command()
                        .primaryName("snapshot")
                        .secondaryName("delete")
                        .commandId(CommandEnum.COMMAND_SNAPSHOT_DELETE.getCommandId())
                        .help("Delete a snapshot")
                        .addArgument(new Argument()
                                .name("snapshot-name")
                                .optional(false)
                                .help("Name of the snapshot to delete")));
    }

    public static void snapshotCreate(String jsonpath) {
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

    public static void snapshotDelete(String snapshotName) {
        SnapshotSummaryModel summary = CommandUtils.findSnapshotByName(snapshotName);
        try {
            JobModel job = api.deleteSnapshot(summary.getId());
            DeleteResponseModel deleteResponse = CommandUtils.waitForResponse(
                    api,
                    job,
                    1,
                    DeleteResponseModel.class);
            System.out.printf("Snapshot deleted: %s (%s)\n", snapshotName, deleteResponse.getObjectState().getValue());
        } catch (ApiException ex) {
            System.out.println("Error processing snapshot delete:");
            CommandUtils.printError(ex);
        }
    }

    public static void snapshotShow(String snapshotName) {
        SnapshotSummaryModel summary = CommandUtils.findSnapshotByName(snapshotName);
        DRSnapshot snapshotElement = new DRSnapshot(summary);
        snapshotElement.describe();
    }

}

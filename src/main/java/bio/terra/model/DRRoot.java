package bio.terra.model;

import bio.terra.command.CommandUtils;
import bio.terra.command.DRApis;
import bio.terra.datarepo.client.ApiException;
import bio.terra.datarepo.model.DatasetSummaryModel;
import bio.terra.datarepo.model.EnumerateDatasetModel;
import bio.terra.datarepo.model.EnumerateSnapshotModel;
import bio.terra.datarepo.model.SnapshotSummaryModel;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DRRoot extends DRElement {

    public DRRoot() {
    }

    @Override
    public DRElementType getObjectType() {
        return DRElementType.DR_ELEMENT_TYPE_COLLECTION;
    }

    @Override
    public String getObjectName() {
        return "root";
    }

    @Override
    public String getCreated() {
        return StringUtils.EMPTY;
    }

    @Override
    public String getId() {
        return StringUtils.EMPTY;
    }

    @Override
    public String getDescription() {
        return "Top of data repository object tree";
    }

    @Override
    public DRElement lookup(LinkedList<String> pathParts) {
        if (pathParts.size() == 0) {
            return this;
        }
        String name = pathParts.remove();

        SnapshotSummaryModel snapshotSummaryModel = findSnapshotByName(name);
        if (snapshotSummaryModel != null) {
            return new DRSnapshot(snapshotSummaryModel).lookup(pathParts);
        }

        DatasetSummaryModel datasetSummaryModel = findDatasetByName(name);
        if (datasetSummaryModel != null) {
            return new DRDataset(datasetSummaryModel).lookup(pathParts);
        }

        CommandUtils.printErrorAndExit("Object not found");
        return null; // unreachable
    }

    @Override
    public List<DRElement> enumerate() {
        List<DRElement> elementList = new ArrayList<>();
        try {
            EnumerateSnapshotModel enumerateSnapshotModel = DRApis.getRepositoryApi()
                    .enumerateSnapshots(0, 10000, null, null, null);
            List<SnapshotSummaryModel> snapshots = enumerateSnapshotModel.getItems();
            for (SnapshotSummaryModel snapshot : snapshots) {
                elementList.add(new DRSnapshot(snapshot));
            }

            EnumerateDatasetModel enumerateDatasetModel = DRApis.getRepositoryApi()
                    .enumerateDatasets(0, 10000, null, null, null);
            List<DatasetSummaryModel> datasets = enumerateDatasetModel.getItems();
            for (DatasetSummaryModel dataset : datasets) {
                elementList.add(new DRDataset(dataset));
            }
        } catch (ApiException ex) {
            System.err.println("Error processing root enumeration list:");
            CommandUtils.printErrorAndExit(ex.getMessage());
        }
        return elementList;
    }

    private SnapshotSummaryModel findSnapshotByName(String snapshotName) {
        try {
            EnumerateSnapshotModel enumerateSnapshotModel = DRApis.getRepositoryApi()
                    .enumerateSnapshots(0, 100000, null, null, snapshotName);

            List<SnapshotSummaryModel> datasets = enumerateSnapshotModel.getItems();
            for (SnapshotSummaryModel summary : datasets) {
                if (StringUtils.equals(summary.getName(), snapshotName)) {
                    return summary;
                }
            }
            return null;

        } catch (ApiException ex) {
            throw new IllegalArgumentException("Error processing find dataset by name");
        }
    }

    private DatasetSummaryModel findDatasetByName(String datasetName) {
        try {
            EnumerateDatasetModel enumerateDatasetModel = DRApis.getRepositoryApi().enumerateDatasets(0, 100000, null, null, datasetName);

            List<DatasetSummaryModel> studies = enumerateDatasetModel.getItems();
            for (DatasetSummaryModel summary : studies) {
                if (StringUtils.equals(summary.getName(), datasetName)) {
                    return summary;
                }
            }
            return null;

        } catch (ApiException ex) {
            throw new IllegalArgumentException("Error processing find dataset by name");
        }
    }

}

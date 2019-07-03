package bio.terra.model;

import bio.terra.command.CommandUtils;
import bio.terra.command.DRApis;
import bio.terra.datarepo.client.ApiException;
import bio.terra.datarepo.model.DatasetSummaryModel;
import bio.terra.datarepo.model.EnumerateDatasetModel;
import bio.terra.datarepo.model.EnumerateStudyModel;
import bio.terra.datarepo.model.StudySummaryModel;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DRRoot extends DRElement {

    public DRRoot() {
    }

    @Override
    public DRElementType getObjectType() {
        return DRElementType.DR_ELEMENT_TYPE_CONTAINER;
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

        DatasetSummaryModel datasetSummaryModel = findDatasetByName(name);
        if (datasetSummaryModel != null) {
            return new DRDataset(datasetSummaryModel).lookup(pathParts);
        }

        StudySummaryModel studySummaryModel = findStudyByName(name);
        if (studySummaryModel != null) {
            return new DRStudy(studySummaryModel).lookup(pathParts);
        }

        CommandUtils.printErrorAndExit("Object not found");
        return null; // unreachable
    }

    @Override
    public List<DRElement> enumerate() {
        List<DRElement> elementList = new ArrayList<>();
        try {
            EnumerateDatasetModel enumerateDatasetModel = DRApis.getRepositoryApi()
                    .enumerateDatasets(0, 10000, null, null, null);
            List<DatasetSummaryModel> datasets = enumerateDatasetModel.getItems();
            for (DatasetSummaryModel dataset : datasets) {
                elementList.add(new DRDataset(dataset));
            }

            EnumerateStudyModel enumerateStudyModel = DRApis.getRepositoryApi()
                    .enumerateStudies(0, 10000, null, null, null);
            List<StudySummaryModel> studies = enumerateStudyModel.getItems();
            for (StudySummaryModel study : studies) {
                elementList.add(new DRStudy(study));
            }
        } catch (ApiException ex) {
            System.err.println("Error processing root enumeration list:");
            CommandUtils.printErrorAndExit(ex.getMessage());
        }
        return elementList;
    }

    private DatasetSummaryModel findDatasetByName(String datasetName) {
        try {
            EnumerateDatasetModel enumerateDatasetModel = DRApis.getRepositoryApi()
                    .enumerateDatasets(0, 100000, null, null, datasetName);

            List<DatasetSummaryModel> datasets = enumerateDatasetModel.getItems();
            for (DatasetSummaryModel summary : datasets) {
                if (StringUtils.equals(summary.getName(), datasetName)) {
                    return summary;
                }
            }
            return null;

        } catch (ApiException ex) {
            throw new IllegalArgumentException("Error processing find dataset by name");
        }
    }

    private StudySummaryModel findStudyByName(String studyName) {
        try {
            EnumerateStudyModel enumerateStudyModel = DRApis.getRepositoryApi().enumerateStudies(0, 100000, null, null, studyName);

            List<StudySummaryModel> studies = enumerateStudyModel.getItems();
            for (StudySummaryModel summary : studies) {
                if (StringUtils.equals(summary.getName(), studyName)) {
                    return summary;
                }
            }
            return null;

        } catch (ApiException ex) {
            throw new IllegalArgumentException("Error processing find study by name");
        }
    }

}

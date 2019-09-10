package bio.terra.model;

import bio.terra.command.CommandUtils;
import bio.terra.command.DRApis;
import bio.terra.datarepo.client.ApiException;
import bio.terra.datarepo.model.DatasetModel;
import bio.terra.datarepo.model.TableModel;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DRDatasetTables extends DRElement {
    private DRDataset datasetElement;

    public DRDatasetTables(DRDataset datasetElement) {
        this.datasetElement = datasetElement;
    }

    @Override
    public DRElementType getObjectType() {
        return DRElementType.DR_ELEMENT_TYPE_CONTAINER;
    }

    @Override
    public String getObjectName() {
        return "tables";
    }

    @Override
    public String getCreated() {
        return datasetElement.getCreated();
    }

    @Override
    public String getId() {
        return StringUtils.EMPTY;
    }

    @Override
    public String getDescription() {
        return "Tables in a study";
    }

    @Override
    public DRElement lookup(LinkedList<String> pathParts) {
        if (pathParts.size() == 0) {
            return this;
        }
        String name = pathParts.remove();

        DatasetModel dataset = getDataset();
        for (TableModel table : dataset.getSchema().getTables()) {
            if (StringUtils.equals(name, table.getName())) {
                return new DRTable(datasetElement, table);
            }
        }
        return null;
    }

    @Override
    public List<DRElement> enumerate() {
        List<DRElement> elementList = new ArrayList<>();
        DatasetModel dataset = getDataset();
        for (TableModel table : dataset.getSchema().getTables()) {
            elementList.add(new DRTable(datasetElement, table));
        }
        return elementList;
    }

// TODO: Do I want to dump all of the tables on a describe of the tables?
    @Override
    public void describe() {
        super.describe();
        DatasetModel dataset = getDataset();
        for (TableModel table : dataset.getSchema().getTables()) {
            new DRTable(datasetElement, table).describe();
        }
    }

    private DatasetModel getDataset() {
        try {
            DatasetModel dataset = DRApis.getRepositoryApi().retrieveDataset(datasetElement.getId());
            return dataset;
        } catch (ApiException ex) {
            System.err.println("Error retrieving dataset");
            CommandUtils.printErrorAndExit(ex.getMessage());
        }
        return null;
    }

}

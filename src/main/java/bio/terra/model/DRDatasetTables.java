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
        return "Tables in a dataset";
    }

    @Override
    public DRElement lookup(LinkedList<String> pathParts) {
        if (pathParts.size() == 0) {
            return this;
        }
        String name = pathParts.remove();

        try {
            DatasetModel dataset = DRApis.getRepositoryApi().retrieveDataset(datasetElement.getId());
            for (TableModel table : dataset.getTables()) {
                if (StringUtils.equals(name, table.getName())) {
                    return new DRTable(datasetElement, table);
                }
            }
        } catch (ApiException ex) {
            System.err.println("Error processing dataset table lookup:");
            CommandUtils.printErrorAndExit(ex.getMessage());
        }
        return null;
    }

    @Override
    public List<DRElement> enumerate() {
        List<DRElement> elementList = new ArrayList<>();
        try {
            DatasetModel dataset = DRApis.getRepositoryApi().retrieveDataset(datasetElement.getId());
            for (TableModel table : dataset.getTables()) {
                elementList.add(new DRTable(datasetElement, table));
            }
        } catch (ApiException ex) {
            System.err.println("Error processing dataset enumerate tables:");
            CommandUtils.printErrorAndExit(ex.getMessage());
        }
        return elementList;
    }
}

package bio.terra.model;

import bio.terra.datarepo.model.TableModel;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static bio.terra.model.DRCollectionType.COLLECTION_TYPE_DATASET;

public class DRCollectionTables extends DRElement {

    private DRCollectionType collectionType;
    private String created;
    private List<TableModel> tables;

    public DRCollectionTables(DRCollectionType collectionType,
                              String created,
                              List<TableModel> tables) {
        this.collectionType = collectionType;
        this.created = created;
        this.tables = tables;
    }

    @Override
    public DRElementType getObjectType() {
        return DRElementType.DR_ELEMENT_TYPE_COLLECTION;
    }

    @Override
    public String getObjectName() {
        return "tables";
    }

    @Override
    public String getCreated() {
        return created;
    }

    @Override
    public String getId() {
        return StringUtils.EMPTY;
    }

    @Override
    public String getDescription() {
        return "Tables in a " + (collectionType == COLLECTION_TYPE_DATASET ? "dataset" : "snapshot");
    }

    @Override
    public DRElement lookup(LinkedList<String> pathParts) {
        if (pathParts.size() == 0) {
            return this;
        }
        String name = pathParts.remove();

        for (TableModel table : tables) {
            if (StringUtils.equals(name, table.getName())) {
                return new DRTable(table, created);
            }
        }
        return null;
    }

    @Override
    public List<DRElement> enumerate() {
        List<DRElement> elementList = new ArrayList<>();
        for (TableModel table : tables) {
            elementList.add(new DRTable(table, created));
        }
        return elementList;
    }

// TODO: Do I want to dump all of the tables on a describe of the tables?
    @Override
    public void describe() {
        super.describe();
        for (TableModel table : tables) {
            new DRTable(table, created).describe();
        }
    }

}

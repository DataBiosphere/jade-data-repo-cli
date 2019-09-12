package bio.terra.model;

import bio.terra.command.CommandUtils;
import bio.terra.command.DRApis;
import bio.terra.datarepo.client.ApiException;
import bio.terra.datarepo.model.SnapshotModel;
import bio.terra.datarepo.model.TableModel;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DRSnapshotTables extends DRElement {
    private DRSnapshot snapshotElement;

    public DRSnapshotTables(DRSnapshot snapshotElement) {
        this.snapshotElement = snapshotElement;
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
        return snapshotElement.getCreated();
    }

    @Override
    public String getId() {
        return StringUtils.EMPTY;
    }

    @Override
    public String getDescription() {
        return "Tables in a snapshot";
    }

    @Override
    public DRElement lookup(LinkedList<String> pathParts) {
        if (pathParts.size() == 0) {
            return this;
        }
        String name = pathParts.remove();

        try {
            SnapshotModel snapshot = DRApis.getRepositoryApi().retrieveSnapshot(snapshotElement.getId());
            for (TableModel table : snapshot.getTables()) {
                if (StringUtils.equals(name, table.getName())) {
                    return new DRTable(snapshotElement, table);
                }
            }
        } catch (ApiException ex) {
            System.err.println("Error processing snapshot table lookup:");
            CommandUtils.printErrorAndExit(ex.getMessage());
        }
        return null;
    }

    @Override
    public List<DRElement> enumerate() {
        List<DRElement> elementList = new ArrayList<>();
        try {
            SnapshotModel snapshot = DRApis.getRepositoryApi().retrieveSnapshot(snapshotElement.getId());
            for (TableModel table : snapshot.getTables()) {
                elementList.add(new DRTable(snapshotElement, table));
            }
        } catch (ApiException ex) {
            System.err.println("Error processing snapshot enumerate tables:");
            CommandUtils.printErrorAndExit(ex.getMessage());
        }
        return elementList;
    }
}

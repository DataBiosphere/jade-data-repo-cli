package bio.terra.model;

import bio.terra.command.CommandUtils;
import bio.terra.datarepo.model.SnapshotSummaryModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DRSnapshot extends DRElement {
    private SnapshotSummaryModel summary;

    public DRSnapshot(SnapshotSummaryModel summary) {
        this.summary = summary;
    }

    @Override
    public DRElementType getObjectType() {
        return DRElementType.DR_ELEMENT_TYPE_DATASET;
    }

    @Override
    public String getObjectName() {
        return summary.getName();
    }

    @Override
    public String getCreated() {
        return summary.getCreatedDate();
    }

    @Override
    public String getId() {
        return summary.getId();
    }

    @Override
    public String getDescription() {
        return summary.getDescription();
    }

    @Override
    public DRElement lookup(LinkedList<String> pathParts) {
        if (pathParts.size() == 0) {
            return this;
        }
        String name = pathParts.remove();

        if (StringUtils.equalsIgnoreCase(name, "files")) {
            return new DRSnapshotFiles(this).lookup(pathParts);
        } else if (StringUtils.equalsIgnoreCase(name, "tables")) {
            return new DRSnapshotTables(this).lookup(pathParts);
        }
        CommandUtils.printErrorAndExit("Object not found");
        return null; //unreachabe
    }

    @Override
    public List<DRElement> enumerate() {
        List<DRElement> elementList = new ArrayList<>();
        elementList.add(new DRSnapshotFiles(this));
        elementList.add(new DRSnapshotTables(this));
        return elementList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("summary", summary)
                .toString();
    }
}

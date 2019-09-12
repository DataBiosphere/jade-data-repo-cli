package bio.terra.model;

import bio.terra.command.CommandUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

public class DRSnapshotFiles extends DRElement {
    private DRSnapshot snapshotElement;

    public DRSnapshotFiles(DRSnapshot snapshotElement) {
        this.snapshotElement = snapshotElement;
    }

    @Override
    public DRElementType getObjectType() {
        return DRElementType.DR_ELEMENT_TYPE_CONTAINER;
    }

    @Override
    public String getObjectName() {
        return "files";
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
        return "File system view of files in a snapshot";
    }

    @Override
    public DRElement lookup(LinkedList<String> pathParts) {
        CommandUtils.printErrorAndExit("Snapshot lookup files is not yet implemented");
        return null;
    }

    @Override
    public List<DRElement> enumerate() {
        CommandUtils.printErrorAndExit("Snapshot enumerate files is not yet implemented");
        return null;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }
}

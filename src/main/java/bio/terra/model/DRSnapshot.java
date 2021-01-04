package bio.terra.model;

import bio.terra.command.CommandUtils;
import bio.terra.command.DRApi;
import bio.terra.datarepo.model.SnapshotModel;
import bio.terra.datarepo.model.SnapshotSummaryModel;
import bio.terra.datarepo.model.TableModel;
import bio.terra.tdrwrapper.exception.DataRepoClientException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class DRSnapshot extends DRElement {
  private SnapshotSummaryModel summary;

  public DRSnapshot(SnapshotSummaryModel summary) {
    this.summary = summary;
  }

  @Override
  public DRElementType getObjectType() {
    return DRElementType.DR_ELEMENT_TYPE_SNAPSHOT;
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
      return new DRCollectionFiles(
              DRCollectionType.COLLECTION_TYPE_SNAPSHOT, summary.getId(), summary.getCreatedDate())
          .lookup(pathParts);
    } else if (StringUtils.equalsIgnoreCase(name, "tables")) {
      SnapshotModel snapshot = getSnapshot(summary.getId());
      return new DRCollectionTables(
              DRCollectionType.COLLECTION_TYPE_SNAPSHOT,
              snapshot.getCreatedDate(),
              snapshot.getTables())
          .lookup(pathParts);
    }

    CommandUtils.printErrorAndExit("Object not found");
    return null; // unreachabe
  }

  @Override
  public List<DRElement> enumerate() {
    SnapshotModel snapshot = getSnapshot(summary.getId());
    List<DRElement> elementList = new ArrayList<>();
    elementList.add(
        new DRCollectionFiles(
            DRCollectionType.COLLECTION_TYPE_SNAPSHOT, summary.getId(), summary.getCreatedDate()));
    elementList.add(
        new DRCollectionTables(
            DRCollectionType.COLLECTION_TYPE_SNAPSHOT,
            snapshot.getCreatedDate(),
            snapshot.getTables()));
    return elementList;
  }

  @Override
  protected void describeText() {
    try {
      SnapshotModel snapshot = DRApi.get().retrieveSnapshot(summary.getId());

      System.out.println("name       : " + snapshot.getName());
      System.out.println("description: " + snapshot.getDescription());
      System.out.println("id         : " + snapshot.getId());
      System.out.println("createdDate: " + snapshot.getCreatedDate());
      System.out.println("..Tables");
      for (TableModel table : snapshot.getTables()) {
        new DRTable(table, snapshot.getCreatedDate()).describeText();
      }
    } catch (DataRepoClientException ex) {
      System.out.println("Error processing snapshot describe:");
      CommandUtils.printError(ex);
    }
  }

  @Override
  protected void describeJson() {
    try {
      // fetch the full Snapshot model, instead of using the summary model that is a property of
      // this class
      SnapshotModel snapshot = DRApi.get().retrieveSnapshot(summary.getId());

      CommandUtils.outputPrettyJson(snapshot);
    } catch (DataRepoClientException ex) {
      System.out.println("Error processing snapshot describe:");
      CommandUtils.printError(ex);
    }
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("summary", summary).toString();
  }

  private SnapshotModel getSnapshot(String id) {
    try {
      return DRApi.get().retrieveSnapshot(id);
    } catch (DataRepoClientException ex) {
      System.err.println("Error retrieving snapshot");
      CommandUtils.printErrorAndExit(ex.getMessage());
    }
    return null; // unreachable
  }
}

package bio.terra.model;

import bio.terra.datarepo.model.ColumnModel;
import bio.terra.datarepo.model.TableModel;
import bio.terra.formatting.FormatColumn;
import bio.terra.formatting.TableFormatter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DRTable extends DRElement {
    private DRElement studyOrDataset;
    private TableModel tableModel;

    public DRTable(DRElement studyOrDataset, TableModel tableModel) {
        this.tableModel = tableModel;
        this.studyOrDataset = studyOrDataset;
    }

    @Override
    public DRElementType getObjectType() {
        return DRElementType.DR_ELEMENT_TYPE_TABLE;
    }

    @Override
    public String getObjectName() {
        return tableModel.getName();
    }

    @Override
    public String getCreated() {
        return studyOrDataset.getCreated();
    }

    @Override
    public String getId() {
        return StringUtils.EMPTY;
    }

    @Override
    public String getDescription() {
        return  tableModel.getColumns().size() + " columns";
    }

    @Override
    public List<DRElement> enumerate() {
        return Collections.singletonList(this);
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public void describe() {
        System.out.println("  Table: " + tableModel.getName());

        List<FormatColumn> formatColumns = new ArrayList<>();
        for (ColumnModel column : tableModel.getColumns()) {
            formatColumns.add(new FormatColumn(column));
        }
        TableFormatter<FormatColumn> formatter = new TableFormatter<>(formatColumns, System.out, 4);
        formatter.printTable();
        System.out.println("");
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("studyOrDataset", studyOrDataset)
                .append("tableModel", tableModel)
                .toString();
    }
}

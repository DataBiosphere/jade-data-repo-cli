package bio.terra.formatting;

import bio.terra.datarepo.model.ColumnModel;
import org.apache.commons.lang3.StringUtils;

public class FormatColumn extends TableFormatBase {
    private static final String[] headers         = new String[]{"Name", "Datatype"};
    private static final int[] lengths            = new int[]{    20,     20};
    private static final boolean[] computeLengths = new boolean[]{true,   false};
    private ColumnModel column;

    public FormatColumn(ColumnModel column) {
        super(headers, lengths, computeLengths);
        this.column = column;
    }

    public String getData(int index) {
        switch (index) {
            case 0: return column.getName();
            case 1: {
                String dtype = StringUtils.upperCase(column.getDatatype());
                if (column.isArrayOf()) {
                    dtype = "ARRAY<" + dtype + ">";
                }
                return dtype;
            }
            default:
                throw new IllegalArgumentException("Bad data index");
        }
    }

}

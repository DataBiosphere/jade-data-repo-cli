package bio.terra.formatting;

import org.apache.commons.lang3.StringUtils;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class TableFormatter<T extends TableFormatBase> {
    private List<T> itemList;
    private PrintStream ps;
    private int indent;

    public TableFormatter(List<T> itemList, PrintStream ps, int indent) {
        this.itemList = itemList;
        this.ps = ps;
        this.indent = indent;
    }

    public void printTable() {
        // Build column width array and make array of columns to compute
        List<Integer> widths = new ArrayList<>();
        List<Integer> computeIndices = new ArrayList<>();

        // We use the first item to get the metadata about the table.
        // Can't use statics in an interface...
        T firstItem = itemList.get(0);
        for (int i = 0; i < firstItem.columnCount(); i++) {
            widths.add(firstItem.getLength(i));
            if (firstItem.computeLength(i)) {
                computeIndices.add(i);
            }
        }

        // Do any field size computations
        if (computeIndices.size() > 0) {
            for (T item : itemList) {
                for (Integer idx : computeIndices) {
                    int dataLength = StringUtils.length(item.getData(idx));
                    if (dataLength > widths.get(idx)) {
                        widths.set(idx, dataLength);
                    }
                }
            }
        }

        // Generate the printf format string, the header data array, and the dash data array
        String[] dash = new String[firstItem.columnCount()];
        String[] header = new String[firstItem.columnCount()];
        StringBuilder sb = new StringBuilder();
        if (indent > 0) {
            sb.append(StringUtils.repeat(' ', indent));
        }
        for (int i = 0; i < firstItem.columnCount(); i++) {
            if (i > 0) {
                sb.append(" | ");
            }
            sb.append("%-").append(widths.get(i)).append("s");

            dash[i] = StringUtils.repeat('-', widths.get(i));
            header[i] = firstItem.getHeader(i);
        }
        sb.append('\n');
        String format = sb.toString();

        // Print the result:
        // - header
        // - dash row
        // - data rows
        ps.printf(format, (Object[])header);
        ps.printf(format, (Object[])dash);

        for (T item : itemList) {
            String[] data = new String[item.columnCount()];
            for (int i = 0; i < item.columnCount(); i++)
            data[i] = item.getData(i);
            ps.printf(format, (Object[])data);
        }
    }

}

package bio.terra.formatting;

import java.util.Arrays;

/**
 * TableFormatBase provides a base class used by TableFormatter to get
 * fields from an underlying object. It is up to the implementation to map index
 * to specific fields.
 *
 * You use this by making a derivation of TableFormatBase
 */
public abstract class TableFormatBase {
    private String[] headers;
    private int[] lengths;
    private boolean[] computeLengths;

    public TableFormatBase(String[] headers, int[] lengths, boolean[] computeLengths) {
        this.headers = Arrays.copyOf(headers, headers.length);
        this.lengths = Arrays.copyOf(lengths, lengths.length);
        this.computeLengths = Arrays.copyOf(computeLengths, computeLengths.length);
    }

    public String getHeader(int index) {
        return headers[index];
    }

    public boolean computeLength(int index) {
        return computeLengths[index];
    }

    public int getLength(int index) {
        return lengths[index];
    }

    public int columnCount() {
        return headers.length;
    }

    public abstract String getData(int index);
}

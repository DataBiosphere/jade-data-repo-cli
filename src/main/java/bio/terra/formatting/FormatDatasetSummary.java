package bio.terra.formatting;

import bio.terra.datarepo.model.DatasetSummaryModel;

public class FormatDatasetSummary extends TableFormatBase {
    private static final String[] headers         = new String[] {"Name", "Id",  "Created", "Description"};
    private static final int[] lengths            = new int[]    {20,     36,    27,        30};
    private static final boolean[] computeLengths = new boolean[]{true,   false, false,     true};
    private DatasetSummaryModel summary;

    public FormatDatasetSummary(DatasetSummaryModel summary) {
        super(headers, lengths, computeLengths);
        this.summary = summary;
    }

    public String getData(int index) {
        switch (index) {
            case 0: return summary.getName();
            case 1: return summary.getId();
            case 2: return summary.getCreatedDate();
            case 3: return summary.getDescription();
            default:
                throw new IllegalArgumentException("Bad data index");
        }
    }
}

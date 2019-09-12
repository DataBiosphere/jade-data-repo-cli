package bio.terra.model;

import bio.terra.command.CommandUtils;
import bio.terra.command.DRApis;
import bio.terra.datarepo.client.ApiException;
import bio.terra.datarepo.model.AssetModel;
import bio.terra.datarepo.model.AssetTableModel;
import bio.terra.datarepo.model.DatasetModel;
import bio.terra.datarepo.model.DatasetSummaryModel;
import bio.terra.datarepo.model.RelationshipModel;
import bio.terra.datarepo.model.RelationshipTermModel;
import bio.terra.datarepo.model.TableModel;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DRDataset extends DRElement {
    private DatasetSummaryModel summary;

    public DRDataset(DatasetSummaryModel summary) {
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
            return new DRDatasetFiles(this).lookup(pathParts);
        } else if (StringUtils.equalsIgnoreCase(name, "tables")) {
            return new DRDatasetTables(this).lookup(pathParts);
        }
        CommandUtils.printErrorAndExit("Object not found");
        return null; //unreachabe
    }

    @Override
    public List<DRElement> enumerate() {
        List<DRElement> elementList = new ArrayList<>();
        elementList.add(new DRDatasetFiles(this));
        elementList.add(new DRDatasetTables(this));
        return elementList;
    }

    @Override
    public void describe() {
        try {
            DatasetModel dataset = DRApis.getRepositoryApi().retrieveDataset(summary.getId());

            System.out.println("name       : " + dataset.getName());
            System.out.println("description: " + dataset.getDescription());
            System.out.println("id         : " + dataset.getId());
            System.out.println("createdDate: " + dataset.getCreatedDate());
            System.out.println("..Tables");
            for (TableModel table : dataset.getSchema().getTables()) {
                new DRTable(this, table).describe();
            }
            System.out.println("..Relationships");
            printRelationships(dataset.getSchema().getRelationships());
            System.out.println("..Assets");
            printAssets(dataset.getSchema().getAssets());
        } catch (ApiException ex) {
            System.out.println("Error processing dataset show:");
            CommandUtils.printError(ex);
        }
    }

    private void printRelationships(List<RelationshipModel> relationships) {
        int maxFromLen = 0;
        int maxToLen = 0;
        int maxNameLen = 0;
        for (RelationshipModel relationship : relationships) {
            if (maxNameLen < StringUtils.length(relationship.getName())) {
                maxNameLen = StringUtils.length(relationship.getName());
            }
            String sterm = formatRelationshipTerm(relationship.getFrom());
            if (maxFromLen < StringUtils.length(sterm)) {
                maxFromLen = StringUtils.length(sterm);
            }
            sterm = formatRelationshipTerm(relationship.getTo());
            if (maxToLen < StringUtils.length(sterm)) {
                maxToLen = StringUtils.length(sterm);
            }
        }
        String fmt = String.format("  %%-%ds: %%-%ds --> %%-%ds\n", maxNameLen, maxFromLen, maxToLen);

        for (RelationshipModel relationship : relationships) {
            System.out.printf(fmt,
                    relationship.getName(),
                    formatRelationshipTerm(relationship.getFrom()),
                    formatRelationshipTerm(relationship.getTo()));
        }
        System.out.println("");
    }

    private String formatRelationshipTerm(RelationshipTermModel term) {
        return term.getTable() + "." + term.getColumn();
    }

    private void printAssets(List<AssetModel> assets) {
        for (AssetModel asset : assets) {
            System.out.println("  Asset: " + asset.getName());
            System.out.println("    root: " + asset.getRootTable() + "." + asset.getRootColumn());
            System.out.println("    tables:");
            for (AssetTableModel assetTable : asset.getTables()) {
                System.out.println("      " + assetTable.getName());
            }
            System.out.println("    follow:");
            for (String follow : asset.getFollow()) {
                System.out.println("      " + follow);
            }
        }
    }

}

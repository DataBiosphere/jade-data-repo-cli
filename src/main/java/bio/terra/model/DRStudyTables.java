package bio.terra.model;

import bio.terra.command.CommandUtils;
import bio.terra.command.DRApis;
import bio.terra.datarepo.client.ApiException;
import bio.terra.datarepo.model.StudyModel;
import bio.terra.datarepo.model.TableModel;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DRStudyTables extends DRElement {
    private DRStudy studyElement;

    public DRStudyTables(DRStudy studyElement) {
        this.studyElement = studyElement;
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
        return studyElement.getCreated();
    }

    @Override
    public String getId() {
        return StringUtils.EMPTY;
    }

    @Override
    public String getDescription() {
        return "Tables in a study";
    }

    @Override
    public DRElement lookup(LinkedList<String> pathParts) {
        if (pathParts.size() == 0) {
            return this;
        }
        String name = pathParts.remove();

        StudyModel study = getStudy();
        for (TableModel table : study.getSchema().getTables()) {
            if (StringUtils.equals(name, table.getName())) {
                return new DRTable(studyElement, table);
            }
        }
        return null;
    }

    @Override
    public List<DRElement> enumerate() {
        List<DRElement> elementList = new ArrayList<>();
        StudyModel study = getStudy();
        for (TableModel table : study.getSchema().getTables()) {
            elementList.add(new DRTable(studyElement, table));
        }
        return elementList;
    }

// TODO: Do I want to dump all of the tables on a describe of the tables?
    @Override
    public void describe() {
        super.describe();
        StudyModel study = getStudy();
        for (TableModel table : study.getSchema().getTables()) {
            new DRTable(studyElement, table).describe();
        }
    }

    private StudyModel getStudy() {
        try {
            StudyModel study = DRApis.getRepositoryApi().retrieveStudy(studyElement.getId());
            return study;
        } catch (ApiException ex) {
            System.err.println("Error retrieving study");
            CommandUtils.printErrorAndExit(ex.getMessage());
        }
        return null;
    }

}

package bio.terra.model;

import bio.terra.command.CommandUtils;
import bio.terra.command.DRApis;
import bio.terra.datarepo.client.ApiException;
import bio.terra.datarepo.model.FSObjectModel;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

public class DRStudyFiles extends DRElement {
    private static final String SLASH = "/";

    private DRStudy studyElement;

    public DRStudyFiles(DRStudy studyElement) {
        this.studyElement = studyElement;
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
        return studyElement.getCreated();
    }

    @Override
    public String getId() {
        return StringUtils.EMPTY;
    }

    @Override
    public String getDescription() {
        return "File system view of files in a study";
    }

    @Override
    public DRElement lookup(LinkedList<String> pathParts) {
        if (pathParts.size() == 0) {
            return this;
        }

        String path;
        if (pathParts.size() == 1) {
            path = SLASH + pathParts.get(0);
        } else {
            path = SLASH + StringUtils.join(pathParts, SLASH);
        }

        try {
            FSObjectModel fsObject = DRApis.getRepositoryApi()
                    .lookupFileObjectByPath(studyElement.getId(), path);
            return new DRFile(fsObject);
        } catch (ApiException ex) {
            CommandUtils.printErrorAndExit("Error processing study files enumeration");
        }
        return null; // unreachable
    }

    @Override
    public List<DRElement> enumerate() {
        FSObjectModel fsObject = getFSObject();
        DRFile rootDir = new DRFile(fsObject);
        return rootDir.enumerate();
    }

    // TODO: Add a describe that does the describe on the top level file directory

    private FSObjectModel getFSObject() {
        try {
            FSObjectModel fsObject = DRApis.getRepositoryApi()
                    .lookupFileObjectByPath(studyElement.getId(), "/");
            return fsObject;
        } catch (ApiException ex) {
            CommandUtils.printErrorAndExit("Error getting root file object");
        }
        return null; // unreachable
    }

}

package bio.terra.model;

import bio.terra.command.CommandUtils;
import bio.terra.command.DRApis;
import bio.terra.datarepo.client.ApiException;
import bio.terra.datarepo.model.FileModel;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

public class DRDatasetFiles extends DRElement {
    private static final String SLASH = "/";

    private DRDataset datasetElement;

    public DRDatasetFiles(DRDataset datasetElement) {
        this.datasetElement = datasetElement;
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
        return datasetElement.getCreated();
    }

    @Override
    public String getId() {
        return StringUtils.EMPTY;
    }

    @Override
    public String getDescription() {
        return "File system view of files in a dataset";
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
            FileModel fileModel = DRApis.getRepositoryApi()
                    .lookupFileByPath(datasetElement.getId(), path, 1);
            return new DRFile(fileModel);
        } catch (ApiException ex) {
            CommandUtils.printErrorAndExit("Error processing dataset files enumeration");
        }
        return null; // unreachable
    }

    @Override
    public List<DRElement> enumerate() {
        FileModel fileModel = getFileModel();
        DRFile rootDir = new DRFile(fileModel);
        return rootDir.enumerate();
    }

    // TODO: Add a describe that does the describe on the top level file directory

    private FileModel getFileModel() {
        try {
            FileModel fileModel = DRApis.getRepositoryApi()
                    .lookupFileByPath(datasetElement.getId(), "/", 0);
            return fileModel;
        } catch (ApiException ex) {
            CommandUtils.printErrorAndExit("Error getting root file object");
        }
        return null; // unreachable
    }

}

package bio.terra.model;

import bio.terra.command.CommandUtils;
import bio.terra.command.DRApis;
import bio.terra.datarepo.client.ApiException;
import bio.terra.datarepo.model.DirectoryDetailModel;
import bio.terra.datarepo.model.FileModel;
import bio.terra.datarepo.model.FileModelType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static bio.terra.model.DRCollectionType.COLLECTION_TYPE_DATASET;

public class DRCollectionFiles extends DRElement {
    private static final String SLASH = "/";

    private DRCollectionType collectionType;
    private String collectionId;
    private String created;

    public DRCollectionFiles(DRCollectionType collectionType,
                             String collectionId,
                             String created) {
        this.collectionType = collectionType;
        this.collectionId = collectionId;
        this.created = created;
    }

    @Override
    public DRElementType getObjectType() {
        return DRElementType.DR_ELEMENT_TYPE_COLLECTION;
    }

    @Override
    public String getObjectName() {
        return "files";
    }

    @Override
    public String getCreated() {
        return created;
    }

    @Override
    public String getId() {
        return StringUtils.EMPTY;
    }

    @Override
    public String getDescription() {
        return "File system view of files in a dataset" +
                (collectionType == COLLECTION_TYPE_DATASET ? "dataset" : "snapshot");
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
            FileModel fileModel = pathLookup(path, 1);
            return new DRFile(collectionType, fileModel);
        } catch (ApiException ex) {
            CommandUtils.printErrorAndExit("Error processing files enumeration");
        }
        return null; // unreachable
    }

    @Override
    public List<DRElement> enumerate() {
        FileModel fileModel = getFileModel();
        DRFile rootDir = new DRFile(collectionType, fileModel);
        return rootDir.enumerate();
    }

    private FileModel getFileModel() {
        try {
            return pathLookup("/", 1);
        } catch (ApiException ex) {
            try {
                // if there are no files or directories in this collection AND it is the top-level files collection,
                // then create an empty directory object here and mark it as already enumerated instead of exiting
                // the process with the file not found exception from the API
                Map<String, String> errorMap = CommandUtils.getObjectMapper()
                        .readValue(ex.getMessage(), new TypeReference<Map<String, String>>() {});
                if (StringUtils.containsIgnoreCase(errorMap.get("message"), "File not found:")) {
                    DirectoryDetailModel directoryDetail = new DirectoryDetailModel()
                            .contents(new ArrayList<FileModel>()).enumerated(true);
                    return new FileModel().fileType(FileModelType.DIRECTORY).directoryDetail(directoryDetail);
                }
            } catch (JsonProcessingException jsonEx) {
                // error parsing as json, just ignore and fall through to the process exit
            }
            CommandUtils.printErrorAndExit("Error getting root file object");
        }
        return null; // unreachable
    }

    private FileModel pathLookup(String path, int depth) throws ApiException {
        if (collectionType == COLLECTION_TYPE_DATASET) {
            return DRApis.getRepositoryApi().lookupFileByPath(collectionId, path, depth);
        }
        return DRApis.getRepositoryApi().lookupSnapshotFileByPath(collectionId, path, depth);
    }

}

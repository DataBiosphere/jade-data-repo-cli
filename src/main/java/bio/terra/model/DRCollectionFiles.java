package bio.terra.model;

import static bio.terra.model.DRCollectionType.COLLECTION_TYPE_DATASET;

import bio.terra.command.CommandUtils;
import bio.terra.command.DRApis;
import bio.terra.datarepo.client.ApiException;
import bio.terra.datarepo.model.DirectoryDetailModel;
import bio.terra.datarepo.model.FileModel;
import bio.terra.datarepo.model.FileModelType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.api.client.http.HttpStatusCodes;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class DRCollectionFiles extends DRElement {
  private static final String SLASH = "/";

  private DRCollectionType collectionType;
  private String collectionId;
  private String created;

  public DRCollectionFiles(DRCollectionType collectionType, String collectionId, String created) {
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
    return "File system view of files in a dataset"
        + (collectionType == COLLECTION_TYPE_DATASET ? "dataset" : "snapshot");
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
      // the pathLookup function above is checking the top-level files collection (path = "/")
      // the code below checks for the case where this returns not found
      // (status code = 404 not found, message property of response body = File not found)
      if (ex.getCode() == HttpStatusCodes.STATUS_CODE_NOT_FOUND) {

        try {
          // parse the response body to build a JSON object
          Map<String, String> errorMap =
              CommandUtils.getObjectMapper()
                  .readValue(ex.getResponseBody(), new TypeReference<Map<String, String>>() {});
          // then extract the message property and check it matches the not found case
          if (StringUtils.containsIgnoreCase(errorMap.get("message"), "File not found:")) {

            // now we've confirmed that this is the case where no files are found
            // create an empty directory object here and mark it as already enumerated
            // this will end the tree enumeration here, instead of hitting the API again to list the
            // directory contents. it also suppresses the API exception so it doesn't leak out to
            // the caller
            DirectoryDetailModel directoryDetail =
                new DirectoryDetailModel().contents(new ArrayList<FileModel>()).enumerated(true);
            return new FileModel()
                .fileType(FileModelType.DIRECTORY)
                .directoryDetail(directoryDetail);
          }
        } catch (JsonProcessingException jsonEx) {
          // error parsing as json, ignore and fall through to the process exit
        }
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

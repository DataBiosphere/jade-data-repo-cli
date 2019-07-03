package bio.terra.model;

import bio.terra.command.CommandUtils;
import bio.terra.command.DRApis;
import bio.terra.datarepo.client.ApiException;
import bio.terra.datarepo.model.DRSChecksum;
import bio.terra.datarepo.model.FSObjectModel;
import bio.terra.datarepo.model.FSObjectModelType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DRFile extends DRElement {
    private FSObjectModel fsObject;

    public DRFile(FSObjectModel fsObject) {
        this.fsObject = fsObject;
    }

    @Override
    public DRElementType getObjectType() {
        if (fsObject.getObjectType() == FSObjectModelType.DIRECTORY) {
            return DRElementType.DR_ELEMENT_TYPE_DIRECTORY;
        }
        return DRElementType.DR_ELEMENT_TYPE_FILE;
    }

    @Override
    public String getObjectName() {
        return CommandUtils.getObjectName(fsObject.getPath());
    }

    @Override
    public String getCreated() {
        return fsObject.getCreated();
    }

    @Override
    public String getId() {
        return fsObject.getObjectId();
    }

    @Override
    public String getDescription() {
        return StringUtils.defaultString(fsObject.getDescription());
    }

    @Override
    public List<DRElement> enumerate() {
        if (fsObject.getObjectType() == FSObjectModelType.DIRECTORY) {
            // There are two cases here.
            // Case 1: this directory came from a top-level get and has contents.
            // Case 2: this directory was a leaf from a get and has no contents.
            // In case 2, we re-retrieve the object to get its contents. And we
            // replace the fsObject in this class.
            // TODO: I don't think this will work in the dataset case. Eventually, we will need
            // to know where the file came from (dataset or study) and do the right retrieval.
            if (fsObject.getDirectoryDetail() == null) {
                try {
                    FSObjectModel enumDir = DRApis.getRepositoryApi()
                                    .lookupFileObjectById(fsObject.getStudyId(), fsObject.getObjectId());
                    fsObject = enumDir;
                } catch (ApiException ex) {
                    throw new IllegalArgumentException("Error processing directory enumerate");
                }
            }

            List<DRElement> elementList = new ArrayList<>();
            for (FSObjectModel item : fsObject.getDirectoryDetail().getContents()) {
                elementList.add(new DRFile(item));
            }

            return elementList;
        } else {
            // Files are leaves and cannot be enumerated further
            return Collections.singletonList(this);
        }
    }

    @Override
    public boolean isLeaf() {
        return (fsObject.getObjectType() == FSObjectModelType.FILE);
    }

    @Override
    public void describe() {
        super.describe();
        System.out.printf(DESCRIBE_FORMAT, "studyId", fsObject.getStudyId());
        System.out.printf(DESCRIBE_FORMAT, "path", fsObject.getPath());
        System.out.printf(DESCRIBE_FORMAT, "size", fsObject.getSize());

        if (fsObject.getObjectType() == FSObjectModelType.FILE) {
            System.out.printf(DESCRIBE_FORMAT, "mimeType", fsObject.getFileDetail().getMimeType());
            for (DRSChecksum checksum : fsObject.getFileDetail().getChecksums()) {
                System.out.printf(DESCRIBE_FORMAT, checksum.getType(), checksum.getChecksum());
            }
            System.out.printf(DESCRIBE_FORMAT, "accessUrl", fsObject.getFileDetail().getAccessUrl());
        } else {
            List<FSObjectModel> contents = fsObject.getDirectoryDetail().getContents();
            if (contents != null) {
                System.out.printf(DESCRIBE_FORMAT, "file count", contents.size());
            }
        }
    }

    public FSObjectModel getFsObject() {
        return fsObject;
    }
}

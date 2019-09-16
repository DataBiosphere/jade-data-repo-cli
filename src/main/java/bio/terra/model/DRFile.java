package bio.terra.model;

import bio.terra.command.CommandUtils;
import bio.terra.command.DRApis;
import bio.terra.datarepo.client.ApiException;
import bio.terra.datarepo.model.DRSChecksum;
import bio.terra.datarepo.model.DirectoryDetailModel;
import bio.terra.datarepo.model.FileModel;
import bio.terra.datarepo.model.FileModelType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DRFile extends DRElement {
    private FileModel fileModel;

    public DRFile(FileModel fileModel) {
        this.fileModel = fileModel;
    }

    @Override
    public DRElementType getObjectType() {
        if (fileModel.getFileType() == FileModelType.DIRECTORY) {
            return DRElementType.DR_ELEMENT_TYPE_DIRECTORY;
        }
        return DRElementType.DR_ELEMENT_TYPE_FILE;
    }

    @Override
    public String getObjectName() {
        return CommandUtils.getObjectName(fileModel.getPath());
    }

    @Override
    public String getCreated() {
        return fileModel.getCreated();
    }

    @Override
    public String getId() {
        return fileModel.getFileId();
    }

    @Override
    public String getDescription() {
        return StringUtils.defaultString(fileModel.getDescription());
    }

    @Override
    public List<DRElement> enumerate() {
        if (fileModel.getFileType() == FileModelType.DIRECTORY) {
            DirectoryDetailModel directoryDetail = fileModel.getDirectoryDetail();
            // If the directory is not enumerated, then enumerate it
            if (!directoryDetail.isEnumerated()) {
                try {
                    FileModel enumDir = DRApis.getRepositoryApi()
                                    .lookupFileById(fileModel.getCollectionId(), fileModel.getFileId(), 1);
                    fileModel = enumDir;
                } catch (ApiException ex) {
                    throw new IllegalArgumentException("Error processing directory enumerate");
                }
            }

            List<DRElement> elementList = new ArrayList<>();
            for (FileModel item : fileModel.getDirectoryDetail().getContents()) {
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
        return (fileModel.getFileType() == FileModelType.FILE);
    }

    @Override
    public void describe() {
        super.describe();
        System.out.printf(DESCRIBE_FORMAT, "collectionId", fileModel.getCollectionId());
        System.out.printf(DESCRIBE_FORMAT, "path", fileModel.getPath());
        System.out.printf(DESCRIBE_FORMAT, "size", fileModel.getSize());
        for (DRSChecksum checksum : fileModel.getChecksums()) {
            System.out.printf(DESCRIBE_FORMAT, checksum.getType(), checksum.getChecksum());
        }

        if (fileModel.getFileType() == FileModelType.FILE) {
            System.out.printf(DESCRIBE_FORMAT, "mimeType", fileModel.getFileDetail().getMimeType());
            System.out.printf(DESCRIBE_FORMAT, "accessUrl", fileModel.getFileDetail().getAccessUrl());
        } else {
            List<FileModel> contents = fileModel.getDirectoryDetail().getContents();
            if (contents != null) {
                System.out.printf(DESCRIBE_FORMAT, "file count", contents.size());
            }
        }
    }

    public FileModel getFileModel() {
        return fileModel;
    }
}

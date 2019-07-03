package bio.terra.command;

// This object holds data from a file, directory, study, or dataset so it can be
// homogeneously presented
public class ListObjectData {
    private String objectType;
    private String objectName;
    private String created;
    private String id;
    private String description;

    public ListObjectData() {
    }

    private String fixNull(String ins) {
        return (ins == null) ? "" : ins;
    }

    public String getObjectType() {
        return objectType;
    }

    public ListObjectData objectType(String objectType) {
        this.objectType = fixNull(objectType);
        return this;
    }

    public String getObjectName() {
        return objectName;
    }

    public ListObjectData objectName(String objectName) {
        this.objectName = fixNull(objectName);
        return this;
    }

    public String getCreated() {
        return created;
    }

    public ListObjectData created(String created) {
        this.created = fixNull(created);
        return this;
    }

    public String getId() {
        return id;
    }

    public ListObjectData id(String id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ListObjectData description(String description) {
        this.description = fixNull(description);
        return this;
    }
}

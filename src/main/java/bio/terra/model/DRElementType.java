package bio.terra.model;

public enum DRElementType {
  DR_ELEMENT_TYPE_DATASET("dataset"),
  DR_ELEMENT_TYPE_SNAPSHOT("snapshot"),
  DR_ELEMENT_TYPE_COLLECTION("group"),
  DR_ELEMENT_TYPE_FILE("file"),
  DR_ELEMENT_TYPE_DIRECTORY("dir"),
  DR_ELEMENT_TYPE_TABLE("table");

  private String name;

  DRElementType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}

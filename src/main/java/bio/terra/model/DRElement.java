package bio.terra.model;

import java.util.LinkedList;
import java.util.List;

public abstract class DRElement {
    public static final String DESCRIBE_FORMAT = "%-12s: %s\n";


    // Abstract interface - provide the basics for all kinds of elements
    public abstract DRElementType getObjectType();
    public abstract String getObjectName();
    public abstract String getCreated();
    public abstract String getId();
    public abstract String getDescription();

    // Each element has to be able to enumerate itself
    public abstract List<DRElement> enumerate();

    // Each element has to be able to lookup a child path. If the element is the last part in the path;
    // that is, the path is empty after it is resolved, then the element returns itself. Otherwise,
    // it recursively calls. When we hit a file system, it looks up in its directory tree, so we don't
    // worry about those. That limits the recursion to at most 3 levels: study, files/tables, file/table.
    // After that, it is an error or it is a leaf.
    public DRElement lookup(LinkedList<String> pathParts) {
        return this;
    }

    // Leaf means a recursive walk should stop at this element. Most elements are not leaves, so we
    // put the common default here.
    public boolean isLeaf() {
        return false;
    }

    // Describe generates output formatted by the object. This doesn't separate presentation from logic,
    // but it is simple. The default version displays the default information.
    public void describe() {
        System.out.printf(DESCRIBE_FORMAT, "name", getObjectName());
        System.out.printf(DESCRIBE_FORMAT, "type", getObjectType().getName());
        System.out.printf(DESCRIBE_FORMAT, "description", getDescription());
        System.out.printf(DESCRIBE_FORMAT, "id", getId());
        System.out.printf(DESCRIBE_FORMAT, "createdDate", getCreated());
    }

}

package bio.terra.command;

import bio.terra.model.DRElement;
import bio.terra.model.DRRoot;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;

// General lookup for objects in the logical hierarchy. The lingua franca is derivations from DRElement
//
// hierarchy is:
//  /                  - (0) contains dataset and snapshot objects
//  /<dataset>/        - (1) contains pseudo-dirs 'files' and 'tables'
//  /<dataset>/files/  - (2) the '/' directory of the file system
//  /<dataset>/tables/ - (2) the tables in the dataset
//
// The lookup is done by recursing through the element types starting at the root.

public class DRLookup {
    private static DRLookup theDRLookup;

    private DRLookup() {
    }

    public static DRLookup getInstance(){
        if(theDRLookup == null){
            theDRLookup = new DRLookup();
        }
        return theDRLookup;
    }

    public DRElement lookup(String inPath) {
        String path = CommandUtils.makeFullPath(inPath);
        String[] pathParts = StringUtils.split(path, CommandUtils.SLASH);
        LinkedList<String> pathList = new LinkedList<>(Arrays.asList(pathParts));
        return new DRRoot().lookup(pathList);
    }

}

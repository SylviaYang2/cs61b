package gitlet;

import java.util.LinkedHashMap;
import java.io.Serializable;
import java.util.ArrayList;

/** The Staging area Class denoting a Staging area object.
 * @author Mengmeng Yang
 */
public class StagingArea implements Serializable {
    /** store the added files. */
    private LinkedHashMap<String, Blobs> addedFiles;
    /** store the removed files. */
    private ArrayList<String> removedFiles;

    /**
     * The Staging area constructor.
     */
    public StagingArea() {
        addedFiles = new LinkedHashMap<>();
        removedFiles = new ArrayList<>();
    }

    /**
     * clear the staging area.
     */
    public void clear() {
        addedFiles.clear();
        removedFiles.clear();
    }

    /**
     * Getter for addedFiles.
     * @return addedFiles
     */
    public LinkedHashMap<String, Blobs> getAdded() {
        return addedFiles;
    }

    /**
     * Getter for removedFiles.
     * @return removedFiles
     */
    public ArrayList<String> getRemoved() {
        return removedFiles;
    }
}

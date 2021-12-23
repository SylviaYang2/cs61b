package gitlet;

import java.io.File;
import java.io.Serializable;

/** The Blobs Class denoting a Blobs object.
 * @author Mengmeng Yang
 */
public class Blobs implements Serializable {
    /** file name. */
    private String _fileName;
    /** blob hash. */
    private String _hash;
    /** blob contents in byte. */
    private byte[] _blobContent;

    /**
     * Constructor for Blobs.
     * @param fileName The given file name
     */
    public Blobs(String fileName) {
        _fileName = fileName;
        File file = new File(fileName);
        _blobContent = Utils.readContents(file);
        _hash = hash();
    }

    /**
     * Compute the blobs hash.
     * @return the blobs hash
     */
    public String hash() {
        return Utils.sha1(_fileName + new String(_blobContent));
    }

    /**
     * Getter for file name.
     * @return file name
     */
    public String getFileName() {
        return _fileName;
    }

    /**
     * Getter for the blobs hash.
     * @return blobs hash
     */
    public String getHash() {
        return _hash;
    }

    /**
     * Getter for the blobs contents.
     * @return blobs contents
     */
    public byte[] getContent() {
        return _blobContent;
    }
}

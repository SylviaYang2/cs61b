package gitlet;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;

/** The Commit Class denoting a Commit object.
 * @author Mengmeng Yang
 */
public class Commit implements Serializable {
    /** commit message. */
    private String _message;
    /** commit timestamp.*/
    private String _timestamp;
    /** commit parent. */
    private String _parent;
    /** commit parent2. */
    private String _parent2;
    /** commit id. */
    private String _commitId;
    /** commit blobs.*/
    private LinkedHashMap<String, Blobs> _fileToBlobs;

    /**
     * The commit constructor.
     * @param message commit message
     * @param parent commit parent
     * @param parent2 commit parent2
     * @param fileToBlobs commit blobs
     */
    public Commit(String message, String parent,
                  String parent2, LinkedHashMap<String, Blobs> fileToBlobs) {
        _message = message;
        _parent = parent;
        _parent2 = parent2;
        if (_parent == null) {
            _timestamp = new SimpleDateFormat(
                    "EEE MMM d HH:mm:ss yyyy Z").format(new Date(0));
        } else {
            ZonedDateTime currentTime = ZonedDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.
                    ofPattern("EEE MMM d HH:mm:ss yyyy Z");
            _timestamp = currentTime.format(formatter);
        }
        _fileToBlobs = fileToBlobs;
        hash();
    }

    /** Compute the commid id.*/
    public void hash() {
        _commitId = Utils.sha1(_message + _timestamp
                + _parent + _fileToBlobs.toString());
    }

    /** Print the commit info. */
    public void printInfo() {
        System.out.println("===\n" + "commit " + _commitId + "\n" + "Date: "
                + _timestamp + "\n" + _message + "\n");
    }

    /**
     * Getter for message.
     * @return commit message
     */
    public String getMessage() {
        return _message;
    }

    /**
     * Getter for timestamp.
     * @return commit timestamp
     */
    public String getTimestamp() {
        return _timestamp;
    }

    /** Getter for parent.
     * @return commit parent
     */
    public String getParent() {
        return _parent;
    }

    /** Getter for parent2.
     * @return commit parent2
     */
    public String getParent2() {
        return _parent2;
    }

    /** Getter for commit id.
     * @return commit id
     */
    public String getCommitId() {
        return _commitId;
    }

    /** Getter for fileToBlobs.
     * @return commit blobs
     */
    public LinkedHashMap<String, Blobs> getBlobs() {
        return _fileToBlobs;
    }
}

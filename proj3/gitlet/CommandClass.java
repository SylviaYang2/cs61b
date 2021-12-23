package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.ArrayList;

/**
 * Consists of the main gitlet commands.
 * @author Mengmeng Yang
 */
public class CommandClass {

    /** Current Working Directory. */
    static final File CWD = new File(".");
    /** .gitlet folder. */
    static final File GITLET_FOLDER = Utils.join(CWD, ".gitlet");
    /** commits folder. */
    static final File COMMIT_DIR = Utils.join(GITLET_FOLDER, "commits");
    /** Staging Area folder. */
    static final File STAGE_DIR = Utils.join(GITLET_FOLDER, "stages");
    /** .Blobs folder. */
    static final File BLOB_DIR = Utils.join(GITLET_FOLDER, "blobs");
    /** Branches folder. */
    static final File BRANCHES_DIR = Utils.join(GITLET_FOLDER, "branches");
    /** File to store the head commit id. */
    static final File HEAD = Utils.join(GITLET_FOLDER, "head");
    /** File to store the current branch name. */
    static final File CURR_BRANCH = Utils.join(BRANCHES_DIR, "curr_branch");
    /** File to store the remote name. */
    static final File REMOTE_DIR = Utils.join(GITLET_FOLDER, "remote");
    /** Remote branch folder. */
    static final File REMOTE_BRANCH = Utils.join(BRANCHES_DIR, "remote");

    /** Current head commit id. */
    private String _headId;
    /** Current branch name. */
    private String _currentBranch;
    /** The current staging area object. */
    private StagingArea stagingArea;

    /** Process Gitlet commands. */
    public CommandClass() {
        try {
            stagingArea = Utils.readObject(Utils.join
                    (STAGE_DIR, "stage"), StagingArea.class);
        } catch (IllegalArgumentException e) {
            stagingArea = new StagingArea();
        }
        try {
            _headId = Utils.readContentsAsString(HEAD);
        } catch (IllegalArgumentException e) {
            _headId = "";
        }
        try {
            _currentBranch = Utils.readContentsAsString(CURR_BRANCH);
        } catch (IllegalArgumentException e) {
            _currentBranch = "";
        }
    }

    /** Initialize gitlet. */
    public void init() throws IOException {
        if (GITLET_FOLDER.exists()) {
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
        } else {
            GITLET_FOLDER.mkdir();
            COMMIT_DIR.mkdir();
            STAGE_DIR.mkdir();
            BLOB_DIR.mkdir();
            BRANCHES_DIR.mkdir();
            HEAD.createNewFile();
            CURR_BRANCH.createNewFile();
            REMOTE_DIR.mkdir();
            Commit commit = new Commit("initial commit",
                    null, null, new LinkedHashMap<>());
            _headId = commit.getCommitId();
            _currentBranch = "master";
            Utils.writeContents(HEAD, _headId);
            Utils.writeContents(CURR_BRANCH, _currentBranch);
            Utils.writeContents(Utils.join
                    (BRANCHES_DIR, _currentBranch), _headId);
            Utils.writeObject(Utils.join
                    (COMMIT_DIR, commit.getCommitId()), commit);
            Utils.writeObject(Utils.join(STAGE_DIR, "stage"), stagingArea);
        }
    }

    /**
     * Add a file to the Staging Area and to the commit blobs.
     * @param fileName File name
     */
    public void add(String fileName) {
        File fileSaved = Utils.join(CWD, fileName);
        if (!fileSaved.exists()) {
            System.out.println("File does not exist.");
        } else {
            Blobs blob = new Blobs(fileName);
            Commit commit = getCommit(_headId);
            LinkedHashMap<String, Blobs> commitBlob = commit.getBlobs();
            if (commitBlob.containsKey(fileName) && commitBlob.get(fileName).
                    getHash().equals(blob.getHash())) {
                stagingArea.getAdded().remove(fileName);
                Utils.writeObject(Utils.join(STAGE_DIR, "stage"), stagingArea);
            } else {
                stagingArea.getAdded().put(fileName, blob);
            }
            if (stagingArea.getRemoved().contains(fileName)) {
                stagingArea.getRemoved().remove(fileName);
            }

            Utils.writeObject(Utils.join(STAGE_DIR, "stage"), stagingArea);
            Utils.writeObject(Utils.join(BLOB_DIR, blob.getHash()), blob);
        }
    }

    /**
     * Create a new Commit and serialize it to a file in COMMIT_DIR.
     * @param message The commit message
     */
    public void commit(String message) {
        if (stagingArea.getAdded().isEmpty()
                && stagingArea.getRemoved().isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        if (message.isBlank()) {
            System.out.print("Please enter a commit message.");
            return;
        }
        Commit parent = getCommit(_headId);
        Set<String> parentTracked = parent.getBlobs().keySet();
        LinkedHashMap<String, Blobs> newBlob = new LinkedHashMap<>();
        for (String files: parentTracked) {
            newBlob.put(files, parent.getBlobs().get(files));
        }
        Set<String> filesStaged = stagingArea.getAdded().keySet();
        for (String files: filesStaged) {
            newBlob.put(files, stagingArea.getAdded().get(files));
        }
        for (String key: stagingArea.getRemoved()) {
            newBlob.remove(key);
        }
        Commit newCommit;
        if (message.split(" ")[0].equals("Merged")) {
            File merge = Utils.join(BRANCHES_DIR, "merge");
            String parent2 = Utils.readContentsAsString(merge);
            newCommit = new Commit(message, _headId, parent2, newBlob);
        } else {
            newCommit = new Commit(message, _headId, null, newBlob);
        }
        stagingArea.clear();
        _headId = newCommit.getCommitId();
        Utils.writeContents(HEAD, _headId);
        Utils.writeContents(Utils.join
                (BRANCHES_DIR, _currentBranch), _headId);
        Utils.writeObject(Utils.join
                (COMMIT_DIR, newCommit.getCommitId()), newCommit);
        Utils.writeObject(Utils.join(
                STAGE_DIR, "stage"), stagingArea);
    }

    /**
     * Remove (Unstage or Untrack) a file.
     * @param fileName File name
     */
    public void rm(String fileName) {
        Commit commit = getCommit(_headId);
        if (stagingArea.getAdded().containsKey(fileName)) {
            stagingArea.getAdded().remove(fileName);
        } else if (commit.getBlobs().containsKey(fileName)) {
            stagingArea.getRemoved().add(fileName);
            stagingArea.getAdded().remove(fileName);
            Utils.restrictedDelete(fileName);
        } else {
            System.out.println("No reason to remove the file.");
        }
        Utils.writeObject(Utils.join(STAGE_DIR, "stage"), stagingArea);
    }

    /** Starting at the current head commit, display information
     * about each commit backwards along the commit tree
     * until the initial commit. */
    public void log() {
        Commit commit = getCommit(_headId);
        commit.printInfo();
        while (commit.getParent() != null) {
            commit = Utils.readObject
                    (Utils.join(COMMIT_DIR, commit.getParent()), Commit.class);
            commit.printInfo();
        }
    }

    /** Displays information about all commits. */
    public void globalLog() {
        List<String> commitFiles = Utils.plainFilenamesIn(COMMIT_DIR);
        for (String sha: commitFiles) {
            Commit commit = Utils.readObject
                    (Utils.join(COMMIT_DIR, sha), Commit.class);
            commit.printInfo();
        }
    }

    /**
     * Prints out the ids of all commits that have the given commit message.
     * @param input The given commit message
     */
    public void find(String input) {
        boolean find = false;
        List<String> commits = Utils.plainFilenamesIn(COMMIT_DIR);
        for (String id: commits) {
            if (getCommit(id).getMessage().equals(input)) {
                System.out.println(id);
                find = true;
            }
        }
        if (!find) {
            System.out.println("Found no commit with that message.");
        }
    }

    /** Display information about branches, staged files, removed files,
     * modified niy not staged files, and untracked files.
     */
    public void status() {
        List<String> branchesFiles = Utils.plainFilenamesIn(BRANCHES_DIR);
        ArrayList<String> branches = new ArrayList<>(branchesFiles);
        branches.remove(CURR_BRANCH.getName());
        branches.remove(_currentBranch);
        branches.add("*" + _currentBranch);
        Collections.sort(branches);
        System.out.println("=== Branches ===");
        for (String branch: branches) {
            System.out.println(branch);
        }
        System.out.println();
        List<String> staged = new ArrayList<>();
        staged.addAll(stagingArea.getAdded().keySet());
        Collections.sort(staged);
        System.out.println("=== Staged Files ===");
        for (String file: staged) {
            System.out.println(file);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (String rmFiles: stagingArea.getRemoved()) {
            System.out.println(rmFiles);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        LinkedHashMap<String, String> modified = getModifiedFiles();
        for (String file: modified.keySet()) {
            System.out.println(file + modified.get(file));
        }
        System.out.println();
        System.out.println("=== Untracked Files ===");
        ArrayList<String> untracked = getUntrackedFiles();
        for (String file: untracked) {
            System.out.println(file);
        }
        System.out.println();
    }

    /**
     * Deletes the branch with the given name.
     * @param branchName The given branch name
     */
    public void rmbranch(String branchName) {
        if (branchName.equals(_currentBranch)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        File branch = Utils.join(BRANCHES_DIR, branchName);
        boolean isDeleted = branch.delete();
        if (!isDeleted) {
            System.out.println("A branch with that name does not exist.");
        }
    }

    /**
     * Checkout the file(s) in the commit with the given id.
     * @param commitId The given commit id
     * @param fileName The given file name
     */
    public void checkout(String commitId, String fileName) {
        String foundId = findCommitId(commitId);
        if (foundId == null) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit commit = getCommit(foundId);
        LinkedHashMap<String, Blobs> commitBlob = commit.getBlobs();
        if (!commitBlob.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        Blobs blob = commit.getBlobs().get(fileName);
        Utils.writeContents(Utils.join(CWD, fileName), blob.getContent());
        Utils.writeObject(Utils.join(BLOB_DIR, blob.getHash()), blob);
    }

    /**
     * Checkout a file.
     * @param fileName The given file name
     */
    public void checkout(String fileName) {
        Commit commit = getCommit(_headId);
        LinkedHashMap<String, Blobs> commitBlob = commit.getBlobs();
        if (!commitBlob.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        Blobs blob = commit.getBlobs().get(fileName);
        Utils.writeContents(Utils.join(CWD, fileName), blob.getContent());
        Utils.writeObject(Utils.join(BLOB_DIR, blob.getHash()), blob);
    }

    /**
     * Checkout a branch.
     * @param branchName The given branch name
     */
    public void checkoutBranch(String branchName) {
        if (branchName.contains("/")) {
            branchName = branchName.replace("/", "");
        }
        File branch = Utils.join(BRANCHES_DIR, branchName);
        if (!branch.exists()) {
            System.out.println("No such branch exists.");
            return;
        }
        if (branchName.equals(_currentBranch)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        String idToGet = Utils.readContentsAsString(branch);
        Commit currCommit = getCommit(_headId);
        Commit givenCommit = getCommit(idToGet);
        List<String> workingFiles = Utils.plainFilenamesIn(CWD);
        if (untrackedFileError(currCommit, givenCommit, workingFiles)) {
            return;
        }
        for (String file: givenCommit.getBlobs().keySet()) {
            byte[] blobContent = givenCommit.getBlobs().get(file).getContent();
            Utils.writeContents(Utils.join(CWD, file), blobContent);
        }
        for (String file: workingFiles) {
            if (currCommit.getBlobs().containsKey(file)
                    && !givenCommit.getBlobs().containsKey(file)) {
                Utils.restrictedDelete(file);
            }
        }
        stagingArea.clear();
        Utils.writeObject(Utils.join(STAGE_DIR, "stage"), stagingArea);
        Utils.writeContents(CURR_BRANCH, branchName);
        Utils.writeContents(HEAD, idToGet);
    }

    /**
     * Checks out all the files tracked by the given commit.
     * @param id The given commit id
     */
    public void reset(String id) throws IOException {
        String tempCurrBranch = _currentBranch;
        String foundId = findCommitId(id);
        if (foundId == null) {
            System.out.println("No commit with that id exists.");
            return;
        }
        branchHelper("temp", id);
        checkoutBranch("temp");
        Utils.writeContents(CURR_BRANCH, tempCurrBranch);
        Utils.writeContents(HEAD, id);
        Utils.writeContents(Utils.join(BRANCHES_DIR, _currentBranch), id);
        Utils.join(BRANCHES_DIR, "temp").delete();
    }

    /**
     * Creates a new branch with the given name,
     * and points it at the current head node.
     * @param branchName The given branch name
     */
    public void branch(String branchName) throws IOException {
        branchHelper(branchName, _headId);
    }

    /**
     * The helper branch method.
     * @param branchName The given branch name
     * @param id The given commit id
     */
    private void branchHelper(String branchName, String id) throws IOException {
        File branch = Utils.join(BRANCHES_DIR, branchName);
        if (branch.exists()) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        if (branchName.contains("/")) {
            branchName = branchName.replace("/", "");
            branch = Utils.join(BRANCHES_DIR, branchName);
        }
        Utils.writeContents(branch, id);
    }

    /**
     * Report the untracked file error.
     * @param curr The current commit
     * @param given The given commit
     * @param list The list of files in the CWD
     * @return if the error exists or not
     */
    public boolean untrackedFileError(Commit curr,
                                      Commit given, List<String> list) {
        for (String file: list) {
            if (!curr.getBlobs().containsKey(file)
                    && given.getBlobs().containsKey(file)) {
                System.out.println("There is an untracked file in the way;"
                        + " delete it, or add and commit it first.");
                return true;
            }
        }
        return false;
    }

    /**
     * Report the errors in the merge command.
     * @param split The commit at the split point
     * @param curr The current commit
     * @param given The given commit
     * @param branchName The given branch name
     * @return if the error exists or not
     */
    public boolean mergeError2(Commit split, Commit curr,
                               Commit given, String branchName) {
        if (split.getCommitId().equals(given.getCommitId())) {
            System.out.println("Given branch is an "
                    + "ancestor of the current branch.");
            return true;
        }
        if (split.getCommitId().equals(curr.getCommitId())) {
            checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            return true;
        }
        return false;
    }

    /**
     * Compare if two blobs' content are the .
     * @param b1 Blob 1
     * @param b2 Blob 2
     * @return if two blobs' content are the same
     */
    public boolean cp(Blobs b1, Blobs b2) {
        return b1.getHash().equals(b2.getHash());
    }

    /**
     * The first part of the merge command.
     * @param branchName The given branch name
     */
    public void merge(String branchName) {
        if (mergeError(branchName)) {
            return;
        }
        File branch = Utils.join(BRANCHES_DIR, branchName);
        String branchId = Utils.readContentsAsString(branch);
        Commit curr = getCommit(_headId);
        Commit given = getCommit(branchId);
        List<String> workingFiles = Utils.plainFilenamesIn(CWD);
        if (untrackedFileError(curr, given, workingFiles)) {
            return;
        }
        Commit split = findSplit(curr, given);
        if (mergeError2(split, curr, given, branchName)) {
            return;
        }
        merge2(curr, given, split, branchId, branchName);
    }

    /**
     * The second part of the merge command.
     * @param curr The current commit
     * @param given The given commit
     * @param split The commit at the split point
     * @param branchId The given branch head's id
     * @param branchName The given branch name
     */
    public void merge2(Commit curr, Commit given, Commit split,
                       String branchId, String branchName) {
        boolean hasConflict = false;
        for (String file: curr.getBlobs().keySet()) {
            Blobs curBlob = curr.getBlobs().get(file);
            Blobs givBlob = given.getBlobs().get(file);
            if (split.getBlobs().containsKey(file)) {
                Blobs spBlob = split.getBlobs().get(file);
                if (givBlob != null) {
                    if (cp(spBlob, curBlob) && !cp(spBlob, givBlob)) {
                        checkout(branchId, file);
                        add(file);
                        Utils.writeObject(
                                Utils.join(STAGE_DIR, "stage"), stagingArea);
                    }
                    if (!cp(spBlob, curBlob) && !cp(spBlob, givBlob)
                            && !cp(curBlob, givBlob)) {
                        hasConflict = true;
                        writeConflict(file, curBlob, givBlob);
                    }
                } else if (!cp(spBlob, curBlob) && givBlob == null) {
                    hasConflict = true;
                    writeConflictGivenDeleted(file, curBlob);
                } else if (cp(spBlob, curBlob) && givBlob == null) {
                    rm(file);
                }
            }
        }
        for (String file: given.getBlobs().keySet()) {
            Blobs curBlob = curr.getBlobs().get(file);
            Blobs givBlob = given.getBlobs().get(file);
            Blobs spBlob = split.getBlobs().get(file);
            if (spBlob != null && !cp(spBlob, givBlob) && curBlob == null) {
                hasConflict = true;
                writeConflictCurrDeleted(file, givBlob);
            } else if (spBlob == null && curBlob != null
                    && givBlob != null && !cp(curBlob, givBlob)) {
                hasConflict = true;
                writeConflict(file, curBlob, givBlob);
            } else if (spBlob == null && curBlob == null && givBlob != null) {
                checkout(branchId, file);
                add(file);
            }
        }
        if (hasConflict) {
            System.out.println("Encountered a merge conflict.");
        }
        Utils.writeContents(Utils.join(BRANCHES_DIR, "merge"), branchId);
        commit("Merged " + branchName + " into " + _currentBranch + ".");
        Utils.join(BRANCHES_DIR, "merge").delete();
    }

    /**
     * Saves the given login information under the given remote name.
     * @param name the remote name
     * @param path name of remote directory
     * @throws IOException
     */
    public void addremote(String name, String path) throws IOException {
        if (Utils.join(REMOTE_DIR, name).exists()) {
            System.out.println("A remote with that name already exists.");
            return;
        }
        String newPath = path.replace("/", java.io.File.separator);
        File remote = Utils.join(REMOTE_DIR, name);
        remote.createNewFile();
        Utils.writeContents(remote, newPath);
    }

    /**
     *  Remove information associated with the given remote name.
     * @param name the remote name
     */
    public void rmremote(String name) {
        if (!Utils.join(REMOTE_DIR, name).exists()) {
            System.out.println("A remote with that name does not exist.");
        } else {
            Utils.join(REMOTE_DIR, name).delete();
        }
    }

    /**
     * Attempts to append the current branch's commits to the
     * end of the given branch at the given remote.
     * @param name the remote name
     * @param branchName remote branch name
     */
    public void push(String name, String branchName) {
        String path = Utils.readContentsAsString(Utils.join(REMOTE_DIR, name));
        if (!Utils.join(path).exists()) {
            System.out.println("Remote directory not found.");
            return;
        }
        ArrayList<String> history = new ArrayList<>();
        Commit currTemp = getCommit(_headId);
        Commit curr = getCommit(_headId);
        File remoteBranches = Utils.join(path, "branches");
        File givenBranch = Utils.join(remoteBranches, branchName);
        String givenid = null;
        if (givenBranch.exists()) {
            givenid = Utils.readContentsAsString(givenBranch);
        }
        history.add(_headId);
        while (currTemp.getParent() != null) {
            if (currTemp.getParent2() != null) {
                history.add(currTemp.getParent2());
            }
            history.add(currTemp.getParent());
            currTemp = getCommit(currTemp.getParent());
        }
        if (givenid == null || history.contains(givenid)) {
            File remoteCmPath = Utils.join(path, "commits");
            List<String> remoteCm = Utils.plainFilenamesIn(remoteCmPath);
            for (String cm: history) {
                if (!remoteCm.contains(cm)) {
                    Utils.writeObject(Utils.join
                            (remoteCmPath, cm), getCommit(cm));
                }
            }
            Utils.writeContents(givenBranch, _headId);
        } else {
            System.out.println("Please pull down remote "
                    + "changes before pushing.");
        }
    }

    /**
     * Brings down commits from the remote Gitlet repository
     * into the local Gitlet repository.
     * @param name remote name
     * @param branchName remote branch name
     * @throws IOException
     */
    public void fetch(String name, String branchName) throws IOException {
        String path = Utils.readContentsAsString(Utils.join(REMOTE_DIR, name));
        if (!Utils.join(path).exists()) {
            System.out.println("Remote directory not found.");
            return;
        }
        File remoteBranches = Utils.join(path, "branches");
        File givenBranch = Utils.join(remoteBranches, branchName);
        if (!givenBranch.exists()) {
            System.out.println("That remote does not have that branch.");
            return;
        }
        String newBranch = name + "/" + branchName;
        if (!Utils.join(BRANCHES_DIR, newBranch).exists()) {
            branch(newBranch);
        }
        File remoteCmPath = Utils.join(path, "commits");
        List<String> remoteCm = Utils.plainFilenamesIn(remoteCmPath);
        ArrayList<String> commits = new ArrayList<>();
        Commit curr = getCommit(_headId);
        String rmCommitid = Utils.readContentsAsString(givenBranch);
        commits.add(_headId);
        while (curr.getParent() != null) {
            if (curr.getParent2() != null) {
                commits.add(curr.getParent2());
            }
            commits.add(curr.getParent());
            curr = getCommit(curr.getParent());
        }
        for (String rmcm: remoteCm) {
            if (commits.contains(rmcm)) {
                Utils.writeObject(Utils.join(COMMIT_DIR, rmcm),
                        getCommit(rmcm));
            }
        }
        newBranch = newBranch.replace("/", "");
        File branch = Utils.join(BRANCHES_DIR, newBranch);
        Utils.writeContents(branch, rmCommitid);
    }

    /**
     *  Fetches branch [remote name]/[remote branch name] as for the fetch
     *  command, and then merges that fetch into the current branch.
     * @param name remote name
     * @param branchName remote branch name
     * @throws IOException
     */
    public void pull(String name, String branchName) throws IOException {
        String newBranch = name + "/" + branchName;
        fetch(name, branchName);
        merge(newBranch);
    }
    /**
     * Write the merge conflict where the contents of
     * both branches are changed and different from other.
     * @param fileName The given branch name
     * @param curr The current commit blob
     * @param given The given commit blob
     */
    public void writeConflict(String fileName, Blobs curr, Blobs given) {
        File file = Utils.join(CWD, fileName);
        Utils.writeContents(file, "<<<<<<< HEAD\n",
                curr.getContent(), "=======\n",
                given.getContent(), ">>>>>>>\n");
        add(fileName);
    }

    /**
     * Write the merge conflict where the content of the current commit
     * is changed and the given branch's files are deleted.
     * @param fileName The given file name
     * @param curr The current commit blob
     */
    public void writeConflictGivenDeleted(String fileName, Blobs curr) {
        File file = Utils.join(CWD, fileName);
        Utils.writeContents(file, "<<<<<<< HEAD\n",
                curr.getContent(), "=======\n", ">>>>>>>\n");
        add(fileName);
    }

    /**
     * Write the merge conflict where the content of the given commit
     * is changed and the current commit's files are deleted.
     * @param fileName The given file name
     * @param given The given commit blob
     */
    public void writeConflictCurrDeleted(String fileName, Blobs given) {
        File file = Utils.join(CWD, fileName);
        Utils.writeContents(file, "<<<<<<< HEAD\n", "=======\n",
                given.getContent(), ">>>>>>>\n");
        add(fileName);
    }

    /**
     * Find the split point for the merge command.
     * @param curr The current commit
     * @param given The given commit
     * @return The commit of the splitting point
     */
    public Commit findSplit(Commit curr, Commit given) {
        ArrayList<String> currList = new ArrayList<>();
        currList.add(curr.getCommitId());
        while (curr.getParent() != null) {
            if (curr.getParent2() != null) {
                currList.add(curr.getParent2());
            }
            currList.add(curr.getParent());
            curr = getCommit(curr.getParent());
        }
        HashMap<Integer, String> splitList = new HashMap<>();
        while (Utils.join(COMMIT_DIR, given.getCommitId()).exists()
                && given.getParent() != null) {
            if (currList.contains(given.getCommitId())) {
                splitList.put(currList.
                        indexOf(given.getCommitId()), given.getCommitId());
            }
            if (given.getParent() != null) {
                if (given.getParent2() != null) {
                    if (currList.contains(given.getParent2())) {
                        splitList.put(currList.indexOf(
                                given.getParent2()), given.getParent2());
                    }
                } else if (currList.contains(given.getParent())) {
                    splitList.put(currList.
                            indexOf(given.getParent()), given.getParent());
                }
            }
            given = getCommit(given.getParent());
        }
        ArrayList<Integer> key = new ArrayList<>(splitList.keySet());
        Collections.sort(key);
        return getCommit(splitList.get(key.get(0)));
    }

    /**
     * Report the errors in the merge command.
     * @param branchName The given branch name
     * @return if the error exists or not
     */
    public boolean mergeError(String branchName) {
        if (stagingArea.getAdded().size() != 0
                || stagingArea.getRemoved().size() != 0) {
            System.out.println("You have uncommitted changes.");
            return true;
        }
        File branch = Utils.join(BRANCHES_DIR, branchName);
        if (!branch.exists()) {
            System.out.println("A branch with that name does not exist.");
            return true;
        }
        if (branchName.equals(_currentBranch)) {
            System.out.println(" Cannot merge a branch with itself.");
            return true;
        }
        return false;
    }

    /**
     * Find the given commit id exists or not.
     * @param commitId The given commit id
     * @return return the commit id or null if DNE
     */
    public String findCommitId(String commitId) {
        List<String> commits = Utils.plainFilenamesIn(COMMIT_DIR);
        for (String eachId: commits) {
            if (eachId.substring(0, commitId.length()).equals(commitId)) {
                return eachId;
            }
        }
        return null;
    }

    /**
     * The helper method of getting a specified commit.
     * @param commitId The given commit id
     * @return the specified commit
     */
    public Commit getCommit(String commitId) {
        return Utils.readObject(Utils.join(COMMIT_DIR, commitId), Commit.class);
    }

    /**
     * Get the modified files for status command.
     * @return A LinkedHashMap consisting of modified files
     */
    public LinkedHashMap<String, String> getModifiedFiles() {
        Commit commit = getCommit(_headId);
        LinkedHashMap<String, String> modified = new LinkedHashMap<>();
        LinkedHashMap<String, Blobs> commitBlobs = commit.getBlobs();
        List<String> workingFiles = Utils.plainFilenamesIn(CWD);
        for (String file: workingFiles) {
            Blobs blob = new Blobs(file);
            if ((commitBlobs.containsKey(file)
                    && !commitBlobs.get(file).getHash().equals(blob.getHash())
                    && (!stagingArea.getAdded().containsKey(file)))
                    || (stagingArea.getAdded().containsKey(file)
                    && (!stagingArea.getAdded().get(file).
                    getHash().equals(blob.getHash())))) {
                modified.put(file, " (modified)");
            }
        }
        for (String file: commitBlobs.keySet()) {
            if ((!workingFiles.contains(file)
                    && stagingArea.getAdded().containsKey(file))
                    || ((!workingFiles.contains(file))
                    && (commitBlobs.containsKey(file))
                    && !(stagingArea.getRemoved().contains(file)))) {
                modified.put(file, " (deleted)");
            }
        }
        return modified;
    }

    /**
     * Get the untracked files for status command.
     * @return A LinkedHashMap consisting of untracked files
     */
    public ArrayList<String> getUntrackedFiles() {
        Commit commit = getCommit(_headId);
        ArrayList<String> untracked = new ArrayList<>();
        LinkedHashMap<String, Blobs> commitBlobs = commit.getBlobs();
        List<String> workingFiles = Utils.plainFilenamesIn(CWD);
        for (String file: workingFiles) {
            if (!stagingArea.getAdded().containsKey(file)
                    && !commitBlobs.containsKey(file)) {
                untracked.add(file);
            }
        }
        return untracked;
    }
}

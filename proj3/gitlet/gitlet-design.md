# Gitlet Design Document

**Name**: Mengmeng Yang

## Classes and Data Structures

###*Main
#### Handle and process different command-line arguments.

###*Commands
#### Specific implementations of commands - init, add, commit, log, checkout, etc.
######Static Variables
1. File CWD - the currect working directory
2. File GITLET_FOLDER - under CWD
3. File COMMIT_DIR - under GITLET_FOLDER; write the changed objects 
to the corresponding files within this folder
4. 

###*Commit
#### Store Commit objects, containing information of metadata: Date, Message; tracked files: mappings from name of file to contents of file; and Blob: the content of a file

######Instance Variables
1. String Message - contains the message of a commit
2. String TimeStamp - time at which a commit was created; Assigned 
by the constructor
3. String Parent - the parent commit of a commit object
4. HasMap Blobs - the content of a file


## Algorithms

###*Main

###*Commands
1. init(): Creates a new Gitlet version-control system in the current directory. This system will automatically start with one commit: a commit that contains no files and has the commit message initial commit (just like that, with no punctuation). It will have a single branch: master, which initially points to this initial commit, and master will be the current branch. The timestamp for this initial commit will be 00:00:00 UTC, Thursday, 1 January 1970 in whatever format you choose for dates (this is called "The (Unix) Epoch", represented internally by the time 0.) Since the initial commit in all repositories created by Gitlet will have exactly the same content, it follows that all repositories will automatically share this commit (they will all have the same UID) and all commits in all repositories will trace back to it.
   * set up file structure of the .gitlet directory
   * create .gitlet folder

2. add()

3. commit()
   * Saves a snapshot of tracked files in the current commit and staging area so they can be restored at a later time, creating a new commit. The commit is said to be tracking the saved files. By default, each commit's snapshot of files will be exactly the same as its parent commit's snapshot of files; it will keep versions of files exactly as they are, and not update them. A commit will only update the contents of files it is tracking that have been staged for addition at the time of commit, in which case the commit will now include the version of the file that was staged instead of the version it got from its parent. A commit will save and start tracking any files that were staged for addition but weren't tracked by its parent. Finally, files tracked in the current commit may be untracked in the new commit as a result being staged for removal by the rm command (below).

###*Commit

1. A bunch of getter methods?

## Persistence

1. Write/save onto disk (.gitlet folder) the commits (the new objects 
we created / modified) to the corresponding files within COMMIT_DIR.
   * We can serialize them into bytes that we can eventually write to a 
   specially named file on disk. This can be done with writeObject 
   method from the Utils class.
   * We can use the special hashing function SHA1 provided to generate
   a unique SHA1 ID for each file name, thus we can know which file that
   each commit should go to.
   



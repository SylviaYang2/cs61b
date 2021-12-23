package gitlet;

import java.io.File;
import java.io.IOException;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Mengmeng Yang
 */
public class Main {
    /** Current Working Directory. */
    static final File CWD = new File(".");
    /** .gitlet folder. */
    static final File GITLET_FOLDER = Utils.join(CWD, ".gitlet");
    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) throws IOException {
        CommandClass commands = new CommandClass();
        if (args.length == 0) {
            System.out.println("Please enter a command.");
        } else if (!GITLET_FOLDER.exists() && !args[0].equals("init")) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        } else {
            switch (args[0]) {
            case "init":
                init(commands, args); break;
            case "add":
                add(commands, args); break;
            case "commit":
                commit(commands, args); break;
            case "find":
                find(commands, args); break;
            case "rm":
                rm(commands, args); break;
            case "log":
                log(commands, args); break;
            case "global-log":
                globalLog(commands, args); break;
            case "status":
                status(commands, args); break;
            case "checkout":
                checkout(commands, args); break;
            case "branch":
                branch(commands, args); break;
            case "rm-branch":
                rmbranch(commands, args); break;
            case "reset":
                reset(commands, args); break;
            case "merge":
                merge(commands, args); break;
            case "add-remote":
                addremote(commands, args); break;
            case "rm-remote":
                rmremote(commands, args); break;
            case "push":
                push(commands, args); break;
            case "fetch":
                fetch(commands, args); break;
            case "pull":
                pull(commands, args); break;
            default:
                System.out.println("No command with that name exists.");
            }
        }
        System.exit(0);
    }

    /**
     * init command.
     * @param commands The CommandClass object
     * @param args gitlet.Main ARGS
     * @throws IOException
     */
    public static void init(CommandClass commands,
                            String... args) throws IOException {
        if (validateNumArgs(args, 1)) {
            commands.init();
        }
    }

    /**
     * add command.
     * @param commands The CommandClass object
     * @param args gitlet.Main ARGS
     */
    public static void add(CommandClass commands, String... args) {
        if (validateNumArgs(args, 2)) {
            commands.add(args[1]);
        }
    }

    /**
     * commit command.
     * @param commands The CommandClass object
     * @param args gitlet.Main ARGS
     */
    public static void commit(CommandClass commands, String... args) {
        if (validateNumArgs(args, 2)) {
            commands.commit(args[1]);
        }
    }
    /**
     * find command.
     * @param commands The CommandClass object
     * @param args gitlet.Main ARGS
     */
    public static void find(CommandClass commands, String... args) {
        if (validateNumArgs(args, 2)) {
            commands.find(args[1]);
        }
    }
    /**
     * rm command.
     * @param commands The CommandClass object
     * @param args gitlet.Main ARGS
     */
    public static void rm(CommandClass commands, String... args) {
        if (validateNumArgs(args, 2)) {
            commands.rm(args[1]);
        }
    }
    /**
     * log command.
     * @param commands The CommandClass object
     * @param args gitlet.Main ARGS
     */
    public static void log(CommandClass commands, String... args) {
        if (validateNumArgs(args, 1)) {
            commands.log();
        }
    }
    /**
     * globalLog command.
     * @param commands The CommandClass object
     * @param args gitlet.Main ARGS
     */
    public static void globalLog(CommandClass commands, String... args) {
        if (validateNumArgs(args, 1)) {
            commands.globalLog();
        }
    }
    /**
     * status command.
     * @param commands The CommandClass object
     * @param args gitlet.Main ARGS
     */
    public static void status(CommandClass commands, String... args) {
        if (validateNumArgs(args, 1)) {
            commands.status();
        }
    }
    /**
     * checkout command.
     * @param commands The CommandClass object
     * @param args gitlet.Main ARGS
     */
    public static void checkout(CommandClass commands, String... args) {
        if (args.length == 1) {
            System.out.println("Incorrect operands.");
        } else if (args.length == 2) {
            commands.checkoutBranch(args[1]);
        } else if (args.length == 3) {
            commands.checkout(args[2]);
        } else if (args.length == 4) {
            if (!args[2].equals("--")) {
                System.out.println("Incorrect operands.");
            } else {
                commands.checkout(args[1], args[3]);
            }
        }
    }
    /**
     * branch command.
     * @param commands The CommandClass object
     * @param args gitlet.Main ARGS
     */
    public static void branch(CommandClass commands, String... args)
            throws IOException {
        if (validateNumArgs(args, 2)) {
            commands.branch(args[1]);
        }
    }
    /**
     * rmbranch command.
     * @param commands The CommandClass object
     * @param args gitlet.Main ARGS
     */
    public static void rmbranch(CommandClass commands, String... args) {
        if (validateNumArgs(args, 2)) {
            commands.rmbranch(args[1]);
        }
    }
    /**
     * reset command.
     * @param commands The CommandClass object
     * @param args gitlet.Main ARGS
     */
    public static void reset(CommandClass commands, String... args)
            throws IOException {
        if (validateNumArgs(args, 2)) {
            commands.reset(args[1]);
        }
    }
    /**
     * merge command.
     * @param commands The CommandClass object
     * @param args gitlet.Main ARGS
     */
    public static void merge(CommandClass commands, String... args) {
        if (validateNumArgs(args, 2)) {
            commands.merge(args[1]);
        }
    }

    /**
     * add-remote command.
     * @param commands The CommandClass object
     * @param args gitlet.Main ARGS
     * @throws IOException
     */
    public static void addremote(CommandClass commands, String... args)
            throws IOException {
        if (validateNumArgs(args, 3)) {
            commands.addremote(args[1], args[2]);
        }
    }

    /**
     * rm-remote command.
     * @param commands The CommandClass object
     * @param args gitlet.Main ARGS
     */
    public static void rmremote(CommandClass commands, String... args) {
        if (validateNumArgs(args, 2)) {
            commands.rmremote(args[1]);
        }
    }

    /**
     * push command.
     * @param commands The CommandClass object
     * @param args gitlet.Main ARGS
     */
    public static void push(CommandClass commands, String... args) {
        if (validateNumArgs(args, 3)) {
            commands.push(args[1], args[2]);
        }
    }

    /**
     * fetch command.
     * @param commands The CommandClass object
     * @param args gitlet.Main ARGS
     * @throws IOException
     */
    public static void fetch(CommandClass commands, String... args)
            throws IOException {
        if (validateNumArgs(args, 3)) {
            commands.fetch(args[1], args[2]);
        }
    }

    /**
     * pull command.
     * @param commands The CommandClass object
     * @param args gitlet.Main ARGS
     * @throws IOException
     */
    public static void pull(CommandClass commands, String... args)
            throws IOException {
        if (validateNumArgs(args, 3)) {
            commands.pull(args[1], args[2]);
        }
    }
    /**
     * Checks the number of arguments versus the expected number,
     * throws a RuntimeException if they do not match.
     *
     * @param args Argument array from command line
     * @param n Number of expected arguments
     * @return boolean
     */
    public static boolean validateNumArgs(String[] args, int n) {
        if (args.length == n) {
            return true;
        }
        System.out.println("Incorrect operands.");
        return false;
    }
}

package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Mengmeng Yang
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine machine = readConfig();
        String setting = "";
        String line = "";
        if (!_input.hasNext("(\\*).*")) {
            throw new EnigmaException("Input file doesn't start with *");
        }
        while (_input.hasNext("(\\*).*")) {
            setting = _input.nextLine();
            setUp(machine, setting);
            while (_input.hasNextLine()) {
                line = _input.nextLine();
                if (line.isBlank()) {
                    _output.println();
                } else {
                    if (!line.contains("*")) {
                        line = line.replaceAll(" ", "");
                        printMessageLine(machine.convert(line));
                    } else {
                        setting = line;
                        setUp(machine, setting);
                    }
                }
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        _allRotors = new ArrayList<>();
        try {
            String alphabetRead = _config.next();
            _alphabet = new Alphabet(alphabetRead);
            if (_config.hasNextInt()) {
                _numRotors = _config.nextInt();
            } else {
                throw new EnigmaException(
                        "It should be the number of rotor slots.");
            }
            if (_config.hasNextInt()) {
                _numPawls = _config.nextInt();
            } else {
                throw new EnigmaException("It should be the number of pawls.");
            }
            while (_config.hasNext()) {
                _allRotors.add(readRotor());
            }
            return new Machine(_alphabet, _numRotors, _numPawls, _allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = _config.next().toUpperCase();
            String typeAndNotch = _config.next();
            String notches = "";
            String cycles = "";
            while (_config.hasNext("\\s*\\(.+\\)\\s*")) {
                cycles += _config.next() + " ";
            }
            Permutation perm = new Permutation(cycles, _alphabet);

            if (typeAndNotch.charAt(0) == 'M') {
                if (typeAndNotch.length() < 2) {
                    throw new EnigmaException(
                            "There's no notch for this moving rotor.");
                }
                notches = typeAndNotch.substring(1);
                return new MovingRotor(name, perm, notches);
            } else if (typeAndNotch.charAt(0) == 'N') {
                if (typeAndNotch.length() > 1) {
                    throw new EnigmaException(
                            "There shouldn't be notches for this fixed rotor.");
                }
                return new FixedRotor(name, perm);
            } else if (typeAndNotch.charAt(0) == 'R') {
                if (typeAndNotch.length() > 1) {
                    throw new EnigmaException(
                            "There shouldn't be notches for this reflector.");
                }
                return new Reflector(name, perm);
            } else {
                throw new EnigmaException("Rotor type not found.");
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        Scanner scanner = new Scanner(settings);
        String plugBoard = "";
        String[] rotors = new String[M.numRotors()];
        String ringSetting = "";
        if (scanner.hasNext("\\*.*")) {
            String[] settingStr = settings.split(" ");
            int count = 0;
            for (String ele: settingStr) {
                if (!ele.contains("(")) {
                    count += 1;
                }
            }
            scanner.next();
            for (int i = 0; i < M.numRotors(); i++) {
                rotors[i] = scanner.next();
            }
            M.insertRotors(rotors);
            if (scanner.hasNext()) {
                M.setRotors(scanner.next());
            }
            if (scanner.hasNext() && !scanner.hasNext("\\(.*\\)")) {
                ringSetting = scanner.next();
                M.setRing(ringSetting);
                count += 1;
            }

            while (scanner.hasNext("\\(.*\\)")) {
                plugBoard += scanner.next() + " ";
            }
            if (plugBoard.length() > 0) {
                M.setPlugboard(new Permutation(plugBoard, _alphabet));
            }
        }
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        for (int i = 0; i < msg.length(); i++) {
            _output.print(msg.charAt(i));
            if (i != msg.length() - 1 && ((i + 1) % 5 == 0)) {
                _output.print(" ");
            }
        }
        _output.println();
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** Number of rotors. */
    private int _numRotors;
    /** Number of pawls. */
    private int _numPawls;
    /** ArrayList of all the rotors. */
    private ArrayList<Rotor> _allRotors;
}

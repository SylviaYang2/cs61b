package enigma;

import java.util.ArrayList;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Mengmeng Yang
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cyclesList = new ArrayList<>();
        if (cycles.length() > 0) {
            cycles = cycles.replaceAll("\\s+", "");
            String[] cyclesArray = null;
            cycles = cycles.substring(1, cycles.length() - 1);
            cyclesArray = cycles.split("\\)\\(");
            if (cyclesArray != null) {
                for (String cycle : cyclesArray) {
                    addCycle(cycle);
                }
            }
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        ArrayList<Character> eachCycle = new ArrayList<>();
        if (cycle.length() == 1) {
            eachCycle.add(cycle.charAt(0));
        } else {
            for (int i = 0; i < cycle.length(); i++) {
                eachCycle.add(cycle.charAt(i));
            }
            eachCycle.add(cycle.charAt(0));
        }
        _cyclesList.add(eachCycle);
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char inputChar = _alphabet.toChar(wrap(p));
        for (ArrayList cycle: _cyclesList) {
            if (cycle.contains(inputChar)) {
                char outputChar =
                        (char) cycle.get
                                ((cycle.indexOf(inputChar) + 1) % cycle.size());
                return _alphabet.toInt(outputChar);
            }
        }
        return wrap(p);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char inputChar = _alphabet.toChar(wrap(c));
        char outputChar = '0';
        for (ArrayList cycle: _cyclesList) {
            if (cycle.contains(inputChar)) {
                int indexOfInvert =
                        mod((cycle.indexOf(inputChar) - 1), cycle.size());
                if (indexOfInvert == cycle.size() - 1) {
                    outputChar = (char) cycle.get
                            (mod((cycle.indexOf(inputChar) - 2), cycle.size()));
                } else {
                    outputChar = (char) cycle.get(indexOfInvert);
                }

                return _alphabet.toInt(outputChar);
            }
        }
        return c;
    }

    /**
     * A helper function that performs the same as the wrap function
     * provided, except that this function is taking in an extra parameter
     * size.
     * @param p The integer that should be modded
     * @param size The size that we want to mod by
     * @return The mod result
     */
    private int mod(int p, int size) {
        int r = p % size;
        if (r < 0) {
            r += size;
        }
        return r;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int i = _alphabet.toInt(p);
        return _alphabet.toChar(permute(i));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        int i = _alphabet.toInt(c);
        return _alphabet.toChar(invert(i));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        int count = 0;
        for (ArrayList cycle: _cyclesList) {
            count += (cycle.size() - 1);
            if (cycle.size() == 1) {
                return false;
            }
        }
        return (count == alphabet().size());
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** An ArrayList to store all the cycles. */
    private ArrayList<ArrayList> _cyclesList;
}

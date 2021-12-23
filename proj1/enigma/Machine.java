package enigma;

import java.util.ArrayList;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Mengmeng Yang
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = new ArrayList<>();
        for (Rotor r: allRotors) {
            _allRotors.add(r);
        }
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _setRotors = new ArrayList<>();
        ArrayList<String> addedNames = new ArrayList<>();
        for (String rotorName: rotors) {
            for (Rotor rotor: _allRotors) {
                if (rotorName.toUpperCase().
                        equals(rotor.name().toUpperCase())) {
                    rotor.set(0);
                    _setRotors.add(rotor);
                    if (addedNames.contains(rotorName)) {
                        throw new EnigmaException("Duplicate rotor names.");
                    } else {
                        addedNames.add(rotorName);
                    }
                }
            }
        }
        if (rotors.length != _setRotors.size()) {
            throw new EnigmaException("Rotor name does not exist.");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (!_setRotors.get(0).reflecting()) {
            throw new EnigmaException(
                    "There's' no reflector at the first slot.");
        }
        if (setting.length() != (numRotors() - 1)) {
            throw new EnigmaException(
                    "There are not enough settings for the rotors.");
        }
        for (int i = 1; i < _numRotors; i++) {
            if (setting.matches("[a-zA-Z]+\\d[a-zA-Z]+")) {
                throw new EnigmaException("Bad number(s) in the setting.");
            }
            if (i >= 1 && i < _numRotors - _pawls) {
                if (_setRotors.get(i).reflecting()) {
                    throw new EnigmaException("Reflector in the wrong place.");
                } else if (_setRotors.get(i).rotates()) {
                    throw new EnigmaException(
                            "Moving rotor in fixed rotor's position.");
                } else {
                    char c = setting.charAt(i - 1);
                    _setRotors.get(i).set(c);
                }
            } else if (i > _numRotors - _pawls - 1) {
                if (_setRotors.get(i).reflecting()) {
                    throw new EnigmaException("Reflector in the wrong place.");
                } else if (_setRotors.get(i).rotates()) {
                    char c = setting.charAt(i - 1);
                    _setRotors.get(i).set(c);
                } else {
                    throw new EnigmaException("There are no moving rotors.");
                }
            }
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        int ch = c;
        advanceHelper();
        if (_plugboard != null) {
            ch = _plugboard.permute(ch);
        }
        for (int i = _numRotors - 1; i >= 0; i--) {
            ch = _setRotors.get(i).convertForward(ch);
        }
        for (int i = 1; i < _numRotors; i++) {
            ch = _setRotors.get(i).convertBackward(ch);
        }
        if (_plugboard != null) {
            ch = _plugboard.invert(ch);
        }
        return ch;
    }

    /** The helper function for checking which rotor needs to be advanced
     * and advance the corresponding rotor(s).
     */
    private void advanceHelper() {
        for (int i = 0; i < _numRotors; i += 1) {
            if ((i == _numRotors - 1)
                    || (_setRotors.get(i).rotates()
                    && _setRotors.get(i + 1).atNotch())) {
                _setRotors.get(i).advance();
                if (i < _numRotors - 1) {
                    _setRotors.get(i + 1).advance();
                    i += 1;
                }
            }
        }
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";
        for (int i = 0; i < msg.length(); i++) {
            result += _alphabet.toChar(convert(_alphabet.toInt(msg.charAt(i))));
        }
        return result;
    }

    /** Set the rings according to RINGSETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. */
    void setRing(String ringSetting) {
        if (ringSetting.length() != (numRotors() - 1)) {
            throw new EnigmaException(
                    "There are not enough settings for the rotors.");
        }
        if (ringSetting.length() == 0) {
            for (int i = 1; i < _numRotors; i++) {
                ringSetting += _alphabet.toChar(0);
            }
        }
        for (int i = 1; i < _numRotors; i++) {
            _setRotors.get(i).setR(ringSetting.charAt(i - 1));
        }
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** The number of rotors. */
    private int _numRotors;
    /** The number of pawls. */
    private int _pawls;
    /** The ArrayList of all the rotors in the config file. */
    private ArrayList<Rotor> _allRotors;
    /** The ArrayList of all the rotors in the input file. */
    private ArrayList<Rotor> _setRotors;
    /** The plugboard. */
    private Permutation _plugboard;
}

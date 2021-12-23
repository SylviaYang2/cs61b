package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }
    @Test
    public void testSize() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        assertEquals(4, p.size());
    }

    @Test
    public void testPermute() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        assertEquals('A', p.permute('B'));
        assertEquals(0, p.permute(1));
        assertEquals(1, p.permute(-1));
        assertEquals(3, p.permute(10));
        Permutation p1 = new Permutation("(BAC)", new Alphabet("ABCD"));
        assertEquals('D', p1.permute('D'));
        assertEquals('B', p1.permute('C'));
        assertEquals(3, p1.permute(3));
        assertEquals(1, p1.permute(2));
        Permutation p2 = new Permutation("(BAC) (D)", new Alphabet("ABCD"));
        assertEquals('D', p2.permute('D'));
        assertEquals('B', p1.permute('C'));
        assertEquals(3, p2.permute(3));
        assertEquals(1, p2.permute(2));
        assertEquals(1, p2.permute(10));
        Permutation p3 = new Permutation("", new Alphabet("ABCD"));
        assertEquals('D', p2.permute('D'));
    }

    @Test
    public void testInvertChar() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        assertEquals('D', p.invert('B'));
        assertEquals(3, p.invert(1));
        assertEquals(2, p.invert(-1));
        assertEquals(0, p.invert(10));
        Permutation p1 = new Permutation("(BAC)", new Alphabet("ABCD"));
        assertEquals('D', p1.invert('D'));
        assertEquals(3, p1.invert(3));
        assertEquals(0, p.invert(10));
    }

    @Test
    public void testDerangement() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        assertEquals(true, p.derangement());
        Permutation p1 = new Permutation("(BAC) (D)", new Alphabet("ABCD"));
        assertEquals(false, p1.derangement());
        Permutation p2 = new Permutation("(BAC)", new Alphabet("ABCD"));
        assertEquals(false, p2.derangement());
    }
}

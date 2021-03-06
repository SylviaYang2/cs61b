import java.io.Reader;
import java.io.IOException;


/** Translating Reader: a stream that is a translation of an
*  existing reader.
*  @author Mengmeng (Sylvia) Yang
*
*  NOTE: Until you fill in the right methods, the compiler will
*        reject this file, saying that you must declare TrReader
* 	     abstract.  Don't do that; define the right methods instead!
*/
public class TrReader extends Reader {
    /** A new TrReader that produces the stream of characters produced
     *  by STR, converting all characters that occur in FROM to the
     *  corresponding characters in TO.  That is, change occurrences of
     *  FROM.charAt(i) to TO.charAt(i), for all i, leaving other characters
     *  in STR unchanged.  FROM and TO must have the same length. */

    private final Reader str;
    private final String from;
    private final String to;

    public TrReader(Reader str, String from, String to) {
        this.str = str;
        this.from = from;
        this.to = to;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int numRead = str.read(cbuf, off, len);
        for (int index = off; index < off + numRead; index++) {
            char c = cbuf[index];
            cbuf[index] = changeCharHelper(c);
        }
        return numRead;
    }

    private char changeCharHelper(char c) {
        int indexInFrom = from.indexOf(c);
        if (indexInFrom == -1) {
            return c;
        } else {
            return to.charAt(indexInFrom);
        }
    }

    @Override
    public void close() throws IOException {

    }

    /* TODO: IMPLEMENT ANY MISSING ABSTRACT METHODS HERE
     */
}

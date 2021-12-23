import java.util.Arrays;
import java.util.List;

/** HW #7, Count inversions.
 *  @author
 */
public class Inversions {

    /** A main program for testing purposes.  Prints the number of inversions
     *  in the sequence ARGS. */
    public static void main(String[] args) {
        System.out.println(inversions(Arrays.asList(args)));
    }

    /** Return the number of inversions of T objects in ARGS. */
    public static <T extends Comparable<? super T>>
        int inversions(List<T> args) {
        int count = 0;
        for (int i = 1; i < args.size(); i++) {
            T elem = args.get(i);
            for (int j = i - 1; j >= 0; j--) {
                if (args.get(j).compareTo(elem) > 0) {
                    count += 1;
                }
            }
        }
        return count;
    }

}

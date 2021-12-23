import java.util.Arrays;

/** HW #7, Two-sum problem.
 * @author
 */
public class Sum {

    /** Returns true iff A[i]+B[j] = M for some i and j. */
    public static boolean sumsTo(int[] A, int[] B, int m) {
        Arrays.sort(A);
        for (int i = 0; i < B.length; i++) {
            int numLeft = m - B[i];
            int indexInA = Arrays.binarySearch(A, numLeft);
            if (indexInA > 0 && indexInA < A.length) {
                if (A[indexInA] + B[i] == m) {
                    return true;
                }
            }
        }
        return false;
    }

}

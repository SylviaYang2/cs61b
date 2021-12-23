package arrays;

/* NOTE: The file Arrays/Utils.java contains some functions that may be useful
 * in testing your answers. */

/** HW #2 */

/** Array utilities.
 *  @author
 */
class Arrays {

    /* C1. */
    /** Returns a new array consisting of the elements of A followed by the
     *  the elements of B. */
    static int[] catenate(int[] A, int[] B) {
        if (A == null && B == null) {
            return null;
        }
        if (A == null) {
            int[] result = new int[B.length];
            System.arraycopy(B, 0, result, 0, B.length);
            return result;
        } else if (B == null) {
            int[] result = new int[A.length];
            System.arraycopy(A, 0, result, 0, A.length);
            return result;
        }
        int[] result = new int[A.length + B.length];
        System.arraycopy(A, 0, result, 0, A.length);
        System.arraycopy(B, 0, result, A.length, B.length);
        return result;
    }

    /* C2. */
    /** Returns the array formed by removing LEN items from A,
     *  beginning with item #START. If the start + len is out of bounds for our array, you
     *  can return null.
     *  Example: if A is [0, 1, 2, 3] and start is 1 and len is 2, the
     *  result should be [0, 3]. */
    static int[] remove(int[] A, int start, int len) {
        if ((start + len) > A.length || A == null) {
            return null;
        }
        int[] result = new int[A.length - len];
        System.arraycopy(A, 0, result, 0, start);
        System.arraycopy(A, start+len, result, start, A.length-start-len);
        return result;
    }

    /* C3. */
    /** Returns the array of arrays formed by breaking up A into
     *  maximal ascending lists, without reordering.
     *  For example, if A is {1, 3, 7, 5, 4, 6, 9, 10}, then
     *  returns the three-element array
     *  {{1, 3, 7}, {5}, {4, 6, 9, 10}}. */
    static int[][] naturalRuns(int[] A) {
        if (A.length == 0) {
            return new int[0][];
        }
        int[][] temp = new int[A.length][];
        int start = 0;
        int stop = 1;
        int count = 0;
        for (;stop < A.length; stop++) {
            if (A[stop-1] >= A[stop]) {
                int[] sublist = new int[stop-start];
                System.arraycopy(A, start, sublist, 0, stop-start);
                temp[count] = sublist;
                count++;
                start = stop;
            }
        }
        int[] sublist = new int[stop-start];
        System.arraycopy(A, start, sublist, 0, stop-start);
        temp[count] = sublist;
        count++;

        int[][] result = new int[count][];
        for (int i = 0; i < count; i++) {
            result[i] = temp[i];
        }
        return result;
    }
}

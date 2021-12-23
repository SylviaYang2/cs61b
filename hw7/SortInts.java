/** HW #7, Distribution counting for large numbers.
 *  @author
 */
public class SortInts {

    /** Sort A into ascending order.  Assumes that 0 <= A[i] < n*n for all
     *  i, and that the A[i] are distinct. */
    static void sort(long[] A) {
        sort(A, A.length);
    }

    static private void sort(long[] a, int k) {
        if (a.length == 0 || k <= 1) {
            return;
        }
        int x = 2;
        long maxNum = a[0];
        for (int i = 1; i < k; i++) {
            maxNum = Math.max(maxNum, a[i]);
        }

        int numOfDig = 0;
        while (maxNum > 0) {
            numOfDig += 1;
            maxNum = maxNum >> x;
        }

        for (int i = 0; i < numOfDig; i++) {
            long[] output = new long[k];

            int MASK = (1 << (i + 1) * x) - 1;
            int[] count = new int[1 << x + 1];
            for (int j = 0; j < k; j++) {
                long digit = (a[j] & MASK) >> (x * i);
                count[(int) (digit + 1)] += 1;
            }
            for (int j = 0; j < count.length - 1; j++) {
                count[j + 1] += count[j];
            }
            for (int j = 0; j < k; j++) {
                long digit = (a[j] & MASK) >> (x * i);
                output[count[(int)digit]++] = a[j];
            }

            for (int j = 0; j < k; j++) {
                a[j] = output[j];
            }
        }
    }
}


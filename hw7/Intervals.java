import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

/** HW #7, Sorting ranges.
 *  @author
  */
public class Intervals {
    /** Assuming that INTERVALS contains two-element arrays of integers,
     *  <x,y> with x <= y, representing intervals of ints, this returns the
     *  total length covered by the union of the intervals. */
    public static int coveredLength(List<int[]> intervals) {
        Collections.sort(intervals, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[0] - o2[0];
            }
        });

        int front = Integer.MIN_VALUE;
        int end = Integer.MIN_VALUE;
        int sum = 0;

        for (int i = 0; i < intervals.size(); i++) {
            if (intervals.get(i)[0] > end) {
                front = intervals.get(i)[0];
                end = intervals.get(i)[1];
                sum += end - front;
            } else if (intervals.get(i)[0] <= end && intervals.get(i)[1] > end) {
                sum += (intervals.get(i)[1] - end);
                end = intervals.get(i)[1];
            }
        }
        return sum;
    }

    /** Test intervals. */
    static final int[][] INTERVALS = {
        {19, 30},  {8, 15}, {3, 10}, {6, 12}, {4, 5},
    };
    /** Covered length of INTERVALS. */
    static final int CORRECT = 23;

    /** Performs a basic functionality test on the coveredLength method. */
    @Test
    public void basicTest() {
        assertEquals(CORRECT, coveredLength(Arrays.asList(INTERVALS)));
    }

    /** Runs provided JUnit test. ARGS is ignored. */
    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(Intervals.class));
    }

}

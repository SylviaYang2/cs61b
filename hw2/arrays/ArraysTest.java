package arrays;

import org.junit.Test;
import static org.junit.Assert.*;

/** FIXME
 *  @author FIXME
 */

public class ArraysTest {
    /** FIXME
     */

    @Test
    public void testCatenate() {
        int[] x1 = new int[]{1, 2, 3};
        int[] y1 = new int[]{4, 5, 6};
        int[] z1 = new int[]{1, 2, 3, 4, 5, 6};
        assertArrayEquals(Arrays.catenate(x1, y1), z1);
        assertArrayEquals(Arrays.catenate(null, null), null);
        assertArrayEquals(Arrays.catenate(null, y1), y1);
        assertArrayEquals(Arrays.catenate(y1, null), y1);
    }

    @Test
    public void testRemove() {
        int[] x1 = new int[]{1, 2, 3, 4, 5, 6, 7};
        int[] y1 = new int[]{1, 5, 6, 7};
        assertArrayEquals(Arrays.remove(x1, 1, 3), y1);
        assertArrayEquals(Arrays.remove(x1, 3, 5), null);
    }

    @Test
    public void testNaturalRuns() {
        int[] x = new int[]{1, 3, 7, 5, 4, 6, 9, 10};
        int[] y = new int[]{7, 9, 12, 12, 12, 10, 10, 4, 5};
        assertArrayEquals(Arrays.naturalRuns(x), new int[][]{{1, 3, 7}, {5}, {4, 6, 9, 10}});
        assertArrayEquals(Arrays.naturalRuns(y), new int[][]{{7, 9, 12}, {12}, {12}, {10}, {10}, {4, 5}});
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ArraysTest.class));
    }
}

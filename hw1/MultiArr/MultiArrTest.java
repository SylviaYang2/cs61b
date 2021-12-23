import static org.junit.Assert.*;
import org.junit.Test;

public class MultiArrTest {

    @Test
    public void testMaxValue() {
        int[][] arr1 = {{1,3,4},{1},{5,6,7,8},{7,9}};
        assertEquals(MultiArr.maxValue(arr1), 9);
    }

    @Test
    public void testAllRowSums() {
        int[][] arr1 = {{1,3,4},{1},{5,6,7,8},{7,9}};
        assertArrayEquals(MultiArr.allRowSums(arr1), new int[] {8,1,26,16});
    }


    /* Run the unit tests in this file. */
    public static void main(String... args) {
        System.exit(ucb.junit.textui.runClasses(MultiArrTest.class));
    }
}

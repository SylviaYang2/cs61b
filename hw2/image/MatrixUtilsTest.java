package image;

import org.junit.Test;
import static org.junit.Assert.*;

/** FIXME
 *  @author FIXME
 */

public class MatrixUtilsTest {
    /** FIXME
     */

    @Test
    public void testAccumulateVertical() {
        double[][] input = {{1000000, 1000000, 1000000, 1000000}, {1000000, 75990, 30003, 1000000}, {1000000, 30002, 103046, 1000000}};
        double[][] output = {{1000000, 1000000, 1000000, 1000000}, {2000000, 1075990, 1030003, 2000000}, {2075990, 1060005, 1133049, 2030003}};
        assertArrayEquals(MatrixUtils.accumulateVertical(input), output);
    }

    @Test
    public void testAccumulate() {
        double[][] input = {{1000000, 1000000, 1000000, 1000000}, {1000000, 75990, 30003, 1000000}, {1000000, 30002, 103046, 1000000}};
        double[][] output = {{1000000, 1000000, 1000000, 1000000}, {2000000, 1075990, 1030003, 2000000}, {2075990, 1060005, 1133049, 2030003}};
        int r = input[0].length;
        int c = input.length;
        int r1 = output[0].length;
        int c1 = output.length;
        double[][] transpose1 = new double[r][c];
        double[][] transpose2 = new double[r1][c1];
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                transpose1[i][j] = input[j][i];
            }
        }
        for (int i = 0; i < r1; i++) {
            for (int j = 0; j < c1; j++) {
                transpose2[i][j] = output[j][i];
            }
        }
        assertArrayEquals(MatrixUtils.accumulate(transpose1, MatrixUtils.Orientation.HORIZONTAL), transpose2);
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(MatrixUtilsTest.class));
    }
}

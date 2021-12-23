package lists;

import image.In;
import org.junit.Test;
import static org.junit.Assert.*;

/** FIXME
 *
 *  @author FIXME
 */

public class ListsTest {
    /** FIXME
     */

    // It might initially seem daunting to try to set up
    // IntListList expected.
    //
    // There is an easy way to get the IntListList that you want in just
    // few lines of code! Make note of the IntListList.list method that
    // takes as input a 2D array.

    @Test
    public void testNaturalRuns() {
//        IntList list1 = IntList.list(1, 3, 7, 5, 4, 6, 9, 10, 10, 11);
//        IntList list2 = IntList.list(2, 3, 3, 5, 6, 7, 3, 10);
//        IntList list3 = IntList.list(2);
//        IntList list4 = null;
//        IntList list5 = new IntList();
//        IntList list6 = IntList.list(7, 9, 12, 12, 12, 10, 10, 4, 5);
//        assertEquals(Lists.naturalRuns(list1), IntListList.list(new int[][] {{1, 3, 7}, {5}, {4, 6, 9, 10}, {10, 11}}));
//        assertEquals(Lists.naturalRuns(list2), IntListList.list(new int[][] {{2, 3}, {3, 5, 6, 7}, {3, 10}}));
//        assertEquals(Lists.naturalRuns(list3), IntListList.list(new int[][] {{2}}));
//        assertEquals(Lists.naturalRuns(list4), null);
//        assertEquals(Lists.naturalRuns(list5), new IntListList(new IntList(), null));
        IntList list6 = IntList.list(7, 9, 12, 12, 12, 10, 10, 4, 5);
        assertEquals(Lists.naturalRuns(list6), IntListList.list(new int[][] {{7, 9, 12}, {12}, {12}, {10}, {10}, {4, 5}}));
    }


    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ListsTest.class));
    }
}

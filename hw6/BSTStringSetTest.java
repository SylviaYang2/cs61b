import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Test of a BST-based String Set.
 * @author
 */
public class BSTStringSetTest  {
    private BSTStringSet set1 = new BSTStringSet();

    @Test
    public void test1() {
        set1.put("a");
        set1.put("b");
        set1.put("c");
        set1.put("d");
        set1.put("a");
        ArrayList list1 = new ArrayList();
        list1.add("a");
        list1.add("b");
        list1.add("c");
        list1.add("d");
        assertEquals(true, set1.contains("a"));
        assertEquals(list1, set1.asList());
        set1.put("v");
        set1.put("A");
        set1.put("r");
        ArrayList list2 = new ArrayList();
        list2.add("A");
        list2.add("a");
        list2.add("b");
        list2.add("c");
        list2.add("d");
        list2.add("r");
        list2.add("v");
        assertEquals(list2, set1.asList());
        assertEquals(false, set1.contains("f"));
    }
}

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Test of a BST-based String Set.
 * @author
 */
public class ECHashStringSetTest  {
    private ECHashStringSet set1 = new ECHashStringSet();

    @Test
    public void test1() {
        set1.put("a");
        set1.put("b");
        set1.put("c");
        set1.put("d");
        ArrayList list1 = new ArrayList();
        list1.add("a");
        list1.add("b");
        list1.add("c");
        list1.add("d");
        assertEquals(true, set1.contains("a"));
        assertEquals(list1, set1.asList());
    }
}

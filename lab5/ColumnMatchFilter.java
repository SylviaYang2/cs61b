/**
 * TableFilter to filter for entries whose two columns match.
 *
 * @author Matthew Owen
 */
public class ColumnMatchFilter extends TableFilter {

    public ColumnMatchFilter(Table input, String colName1, String colName2) {
        super(input);
        index1 = input.colNameToIndex(colName1);
        index2 = input.colNameToIndex(colName2);
    }

    @Override
    protected boolean keep() {
        if (candidateNext().getValue(index1).equals(candidateNext().getValue(index2))) {
            return true;
        }
        return false;
    }

    private int index1;
    private int index2;
}

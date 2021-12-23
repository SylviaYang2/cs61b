/**
 * TableFilter to filter for containing substrings.
 *
 * @author Matthew Owen
 */
public class SubstringFilter extends TableFilter {

    public SubstringFilter(Table input, String colName, String subStr) {
        super(input);
        index = input.colNameToIndex(colName);
        _subStr = subStr;
    }

    @Override
    protected boolean keep() {
        if (candidateNext().getValue(index).contains(_subStr)) {
            return true;
        }
        return false;
    }

    private int index;
    private String _subStr;
}

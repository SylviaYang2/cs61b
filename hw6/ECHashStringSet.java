import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/** A set of String values.
 *  @author
 */
class ECHashStringSet implements StringSet {

    private LinkedList<String>[] _buckets;

    private static int _defaultNumBuckets = 5;

    private int _numEle = 0;

    public ECHashStringSet() {
//        this(_defaultNumBuckets);
        _buckets = (LinkedList<String>[]) new LinkedList[_defaultNumBuckets];
        for (int i = 0; i < _defaultNumBuckets; i += 1) {
            _buckets[i] = new LinkedList<String>();
        }
    }
    public ECHashStringSet(int numBuckets) {
        _buckets = (LinkedList<String>[]) new LinkedList[numBuckets];
        for (int i = 0; i < numBuckets; i += 1) {
            _buckets[i] = new LinkedList<String>();
        }
    }

    @Override
    public void put(String s) {
        _numEle += 1;
        if (s != null) {
            if (_numEle / _buckets.length > 5) {
                resize();
            }
            if (!_buckets[hashing(s)].contains(s)) {
                _buckets[hashing(s)].add(s);
            }
        }
    }

    @Override
    public boolean contains(String s) {
        return _buckets[hashing(s)].contains(s);
    }

    @Override
    public List<String> asList() {
        ArrayList<String> list = new ArrayList<>();
//        for (int i = 0; i < _buckets.length; i++) {
//            if (_buckets[i] != null) {
//                for (int j = 0; j < _buckets[i].size(); j++) {
//                    list.add(_buckets[i].get(j));
//                }
//            }
//        }
        for (LinkedList<String> eachList: _buckets) {
            if (eachList != null) {
                for (String s: eachList) {
                    list.add(s);
                }
            }
        }
        return list;
    }

    private void resize() {
        _numEle = 0;
//        LinkedList<String>[] originalBucket = _buckets;
        int newSize = _buckets.length * 2;
        ECHashStringSet hst = new ECHashStringSet(newSize);
        for (LinkedList<String> eachList: _buckets) {
            if (eachList != null) {
                for (String s: eachList) {
                    hst.put(s);
                }
            }
        }
        _buckets = hst._buckets;
    }

    private int hashing(String s) {
        return (s.hashCode() & 0x7fffffff) % _buckets.length;
    }
}

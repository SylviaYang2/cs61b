
/** Disjoint sets of contiguous integers that allows (a) finding whether
 *  two integers are in the same set and (b) unioning two sets together.  
 *  At any given time, for a structure partitioning the integers 1 to N, 
 *  into sets, each set is represented by a unique member of that
 *  set, called its representative.
 *  @author
 */
public class UnionFind {

    private int[] parents;
    private int[] sizes;
    /** A union-find structure consisting of the sets { 1 }, { 2 }, ... { N }.
     */
    public UnionFind(int N) {
        parents = new int[N+1];
        sizes = new int[N+1];
        for (int i = 1; i <= N ; i++) {
            parents[i] = i;
            sizes[i] = 1;
        }
    }

    /** Return the representative of the set currently containing V.
     *  Assumes V is contained in one of the sets.  */
    public int find(int v) {
        if (v == parents[v]) {
            return v;
        } else {
            parents[v] = find(parents[v]); // path compression
            return parents[v];
        }
    }

    /** Return true iff U and V are in the same set. */
    public boolean samePartition(int u, int v) {
        return find(u) == find(v);
    }

    /** Union U and V into a single set, returning its representative. */
    public int union(int u, int v) {
        if (samePartition(u, v)) {
            return find(v);
        } else {
            int rootU = find(u);
            int rootV = find(v);
            if (sizes[rootU] > sizes[rootV]) {
                parents[rootV] = rootU;
                sizes[rootU] += sizes[rootV];
                return rootU;
            } else {
                parents[rootU] = rootV;
                sizes[rootV] += sizes[rootU];
                return rootV;
            }
        }
    }
}

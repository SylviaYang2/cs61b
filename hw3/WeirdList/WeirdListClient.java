/** Functions to increment and sum the elements of a WeirdList. */
class WeirdListClient {

    /** Return the result of adding N to each element of L. */
    static WeirdList add(WeirdList L, int n) {
        return L.map(returnList -> returnList + n);
    }

    /** Return the sum of all the elements in L. */
    static int sum(WeirdList L) {
        Sum sum = new Sum(0);
        L.map(sum);
        return sum.result();
    }

    private static class Sum implements IntUnaryFunction {
        private int sum;

        private Sum(int initialValue) {
            this.sum = initialValue;
        }

        @Override
        public int apply(int x) {
            sum += x;
            return sum;
        }

        public int result() {
            return sum;
        }
    }

    /* IMPORTANT: YOU ARE NOT ALLOWED TO USE RECURSION IN ADD AND SUM
     *
     * As with WeirdList, you'll need to add an additional class or
     * perhaps more for WeirdListClient to work. Again, you may put
     * those classes either inside WeirdListClient as private static
     * classes, or in their own separate files.

     * You are still forbidden to use any of the following:
     *       if, switch, while, for, do, try, or the ?: operator.
     *
     * HINT: Try checking out the IntUnaryFunction interface.
     *       Can we use it somehow?
     */
}

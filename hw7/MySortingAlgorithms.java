import java.util.Arrays;

import static java.lang.System.arraycopy;

/**
 * Note that every sorting algorithm takes in an argument k. The sorting 
 * algorithm should sort the array from index 0 to k. This argument could
 * be useful for some of your sorts.
 *
 * Class containing all the sorting algorithms from 61B to date.
 *
 * You may add any number instance variables and instance methods
 * to your Sorting Algorithm classes.
 *
 * You may also override the empty no-argument constructor, but please
 * only use the no-argument constructor for each of the Sorting
 * Algorithms, as that is what will be used for testing.
 *
 * Feel free to use any resources out there to write each sort,
 * including existing implementations on the web or from DSIJ.
 *
 * All implementations except Counting Sort adopted from Algorithms,
 * a textbook by Kevin Wayne and Bob Sedgewick. Their code does not
 * obey our style conventions.
 */
public class MySortingAlgorithms {

    /**
     * Java's Sorting Algorithm. Java uses Quicksort for ints.
     */
    public static class JavaSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            Arrays.sort(array, 0, k);
        }

        @Override
        public String toString() {
            return "Built-In Sort (uses quicksort for ints)";
        }
    }

    /** Insertion sorts the provided data. */
    public static class InsertionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            if (array.length == 0 || k <= 1) {
                return;
            }
            for (int i = 1; i < k; i++) {
                int curr = array[i];
                int j;
                for (j = i - 1; j >= 0; j--) {
                    if (array[j] <= curr) {
                        break;
                    }
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }

        @Override
        public String toString() {
            return "Insertion Sort";
        }
    }

    /**
     * Selection Sort for small K should be more efficient
     * than for larger K. You do not need to use a heap,
     * though if you want an extra challenge, feel free to
     * implement a heap based selection sort (i.e. heapsort).
     */
    public static class SelectionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            if (array.length == 0 || k <= 1) {
                return;
            }
            for (int i = 0; i < k; i++) {
                int smallest = array[i];
                int pointer = i;
                int j;
                for (j = i + 1; j < k; j++) {
                    if (array[j] < smallest) {
                        smallest = array[j];
                        pointer = j;
                    }
                }
                array[pointer] = array[i];
                array[i] = smallest;
            }

        }

        @Override
        public String toString() {
            return "Selection Sort";
        }
    }

    /** Your mergesort implementation. An iterative merge
      * method is easier to write than a recursive merge method.
      * Note: I'm only talking about the merge operation here,
      * not the entire algorithm, which is easier to do recursively.
      */
    public static class MergeSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            if (array.length == 0 || k <= 1) {
                return;
            }
            sort(array, 0, k);
        }

        private void sort(int[] array, int low, int high) {
            if (low < high) {
                int mid = (low + high) / 2;
                sort(array, low, mid);
                sort(array, mid + 1, high);
                merge(array, low, mid, high);
            }
        }

        private void merge(int[] array, int low, int mid, int high) {
            for (int i = mid; i < high; i++) {
                int curr = array[i];
                int j;
                for (j = i - 1; j >= low; j--) {
                    if (array[j] <= curr) {
                        break;
                    }
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }

        // may want to add additional methods

        @Override
        public String toString() {
            return "Merge Sort";
        }
    }

    /**
     * Your Counting Sort implementation.
     * You should create a count array that is the
     * same size as the value of the max digit in the array.
     */
    public static class CountingSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME: to be implemented
        }

        // may want to add additional methods

        @Override
        public String toString() {
            return "Counting Sort";
        }
    }

    /** Your Heapsort implementation.
     */
    public static class HeapSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "Heap Sort";
        }
    }

    /** Your Quicksort implementation.
     */
    public static class QuickSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "Quicksort";
        }
    }

    /* For radix sorts, treat the integers as strings of x-bit numbers.  For
     * example, if you take x to be 2, then the least significant digit of
     * 25 (= 11001 in binary) would be 1 (01), the next least would be 2 (10)
     * and the third least would be 1.  The rest would be 0.  You can even take
     * x to be 1 and sort one bit at a time.  It might be interesting to see
     * how the times compare for various values of x. */

    /**
     * LSD Sort implementation.
     */
    public static class LSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {
            if (a.length == 0 || k <= 1) {
                return;
            }
            int x = 2;
            int maxNum = a[0];
            for (int i = 1; i < k; i++) {
                maxNum = Math.max(maxNum, a[i]);
            }

            int numOfDig = 0;
            while (maxNum > 0) {
                numOfDig += 1;
                maxNum = maxNum >> x;
            }

            for (int i = 0; i < numOfDig; i++) {
                int[] output = new int[k];

                int MASK = (1 << (i + 1) * x) - 1;
                int[] count = new int[1 << x + 1];
                for (int j = 0; j < k; j++) {
                    int digit = (a[j] & MASK) >> (x * i);
                    count[digit + 1] += 1;
                }
                for (int j = 0; j < count.length - 1; j++) {
                    count[j + 1] += count[j];
                }
                for (int j = 0; j < k; j++) {
                    int digit = (a[j] & MASK) >> (x * i);
                    output[count[digit]++] = a[j];
                }

                for (int j = 0; j < k; j++) {
                    a[j] = output[j];
                }
            }
        }

        @Override
        public String toString() {
            return "LSD Sort";
        }
    }

    /**
     * MSD Sort implementation.
     */
    public static class MSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "MSD Sort";
        }
    }

    /** Exchange A[I] and A[J]. */
    private static void swap(int[] a, int i, int j) {
        int swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

}

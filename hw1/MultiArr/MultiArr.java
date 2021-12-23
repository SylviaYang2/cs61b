/** Multidimensional array 
 *  @author Zoe Plaxco
 */

public class MultiArr {

    /**
    {{“hello”,"you",”world”} ,{“how”,”are”,”you”}} prints:
    Rows: 2
    Columns: 3
    
    {{1,3,4},{1},{5,6,7,8},{7,9}} prints:
    Rows: 4
    Columns: 4
    */
    public static void printRowAndCol(int[][] arr) {
        //TODO: Your code here!
        System.out.println("Rows: " + arr.length);
        int numCol = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].length > numCol) {
                numCol = arr[i].length;
            }
        }
        System.out.println("Column: " + numCol);
    } 

    /**
    @param arr: 2d array
    @return maximal value present anywhere in the 2d array
    */
    public static int maxValue(int[][] arr) {
        int value = Integer.MIN_VALUE;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                if (arr[i][j] > value) {
                    value = arr[i][j];
                }
            }
        }
        return value;
    }

    /**Return an array where each element is the sum of the 
    corresponding row of the 2d array*/
    public static int[] allRowSums(int[][] arr) {
//        int sum[] = new int[arr.length];
//        for (int i = 0; i < arr.length; i++) {
//            int rowSum = 0;
//            for (int j = 0; j < arr[i].length; j++) {
//                rowSum += arr[i][j];
//            }
//            sum[i] = rowSum;
//        }
//        return sum;
        int[] result = new int[arr.length];
        int total;

        //Loop over the first dimension
        for (int i = 0; i < arr.length; i++) {

            total = 0;//Make sure to re-initialize the total in each iteration

            //For each row calculate the sum and store it in total
            for (int k = 0; k < arr[i].length; k++) {
                total += arr[i][k];
            }

            //When you finish put the result of each row in result[i]
            result[i] = total;
        }
        return result;
    }

    public static void main(String[] args) {
        int[][] arr1 = {{1,3,4},{1},{5,6,7,8},{7,9}};
        printRowAndCol(arr1);
    }
}
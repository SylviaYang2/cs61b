public class hw0 {
    public static int max(int[] a) {
        int maxNum = Integer.MIN_VALUE;
        for (int num: a) {
            if (num > maxNum) {
                maxNum = num;
            }
        }
        return maxNum;
    }


    public static boolean threeSum(int[] a) {
        for (int f: a) {
            for (int g: a) {
                for (int h: a) {
                    if (f + g + h == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean threeSumDistinct(int[] a) {
        for (int f = 0; f < a.length; f++) {
            for (int g = f + 1; g < a.length; g++) {
                for (int h = g + 1; h < a.length; h++) {
                    if (a[f] + a[g] + a[h] == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        int[] a = new int[]{1, 2, 3, 4};
        System.out.println(max(a));
    }
}

public class extra_credit {
    public static void main(String args[]){
        // 0 is a dummy for testing purposes since examples are from 1-to-size
        int[] testArray1 = {0, 10, 8, 9, 4, 3, 5, 6, 2, 1};
        int[] testArray2 = {0, 4, 3, 1};
        String testOutput = "";

        testOutput = String.valueOf(satisfiesMaxHeap(testArray1));
        System.out.println(testOutput);
        testOutput = String.valueOf(satisfiesMaxHeap(testArray2));
        System.out.println(testOutput);
    }

    public static boolean satisfiesMaxHeap(int[] A){
        int i = A.length - 1;

        while(i > 1){
            if(A[i] > A[i/2]){
                return false;
            }

            i--;
        }

        return true;
    }
}
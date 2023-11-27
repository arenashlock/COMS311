import java.util.Arrays;

public class Q1 {
    public static int findMedian(int[] A, int[] B) {

        // ------------------------------ BASE CASES ------------------------------

        if(A.length == 1) {
            return A[0];
        }

        if(B.length == 1) {
            return B[0];
        }

        // ------------------------------ RECURSIVE CALLS ------------------------------

        int midA = A[A.length / 2];
        int midB = B[B.length / 2];

        int[] C;
        int[] D;

        if(midA > midB) {
            // Since midA is greater than midB, that means the median is midA or smaller
            C = Arrays.copyOfRange(A, 0, (A.length / 2) + 1);
            // And the median is midB or bigger
            D = Arrays.copyOfRange(B, (B.length / 2), B.length);
        }

        else {
            // If midA isn't bigger than midB, it HAS to be smaller (due to distinct values, it can't be equal)
            // Therefore, the median is midA or bigger
            C = Arrays.copyOfRange(A, (A.length / 2), A.length);
            // And the median is midB or smaller
            D = Arrays.copyOfRange(B, 0, (B.length / 2) + 1);
        }

        return findMedian(C, D);
    }

    public static void main(String args[]) {
        // [1, 3, 15, 16, 24, 26, 27, 72, 90, 100]
        // Medians: 24, 26
        int[] A = new int[] {1, 16, 24, 26, 72};
        int[] B = new int[] {3, 15, 27, 90, 100};

        // Output: 26
        System.out.println(findMedian(A, B));

        // [3, 15, 27, 34, 90, 100, 245, 364, 723, 750]
        // Medians: 90, 100
        int[] C = new int[] {34, 245, 364, 723, 750};
        int[] D = new int[] {3, 15, 27, 90, 100};

        // Output: 100
        System.out.println(findMedian(C, D));

        // [25, 121, 140, 170, 184, 192, 253, 304, 330, 359, 381, 430, 481, 557, 570, 614, 712, 715, 750, 757, 764, 840, 847, 871, 872, 875, 932, 939, 940, 979]
        // Medians: 570, 614
        int[] E = new int[] {170, 184, 253, 304, 381, 430, 481, 557, 764, 847, 871, 872, 875, 940, 979};
        int[] F = new int[] {25, 121, 140, 192, 330, 359, 570, 614, 712, 715, 750, 757, 840, 932, 939};

        // Output: 614
        System.out.println(findMedian(E, F));
    }
}

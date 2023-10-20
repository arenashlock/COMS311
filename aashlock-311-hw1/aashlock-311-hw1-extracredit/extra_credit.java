public class extra_credit {
    public static void main(String args[]){
        long execution_start = System.nanoTime();
        GCD_1(10234589, 98765431);
        System.out.println(System.nanoTime() - execution_start);
        execution_start = System.nanoTime();
        GCD_2(10234589, 98765431);
        System.out.println(System.nanoTime() - execution_start);
        execution_start = System.nanoTime();

        GCD_1(198491329, 217645177);
        System.out.println(System.nanoTime() - execution_start);
        execution_start = System.nanoTime();
        GCD_2(198491329, 217645177);
        System.out.println(System.nanoTime() - execution_start);
        execution_start = System.nanoTime();

        GCD_1(5915587277L, 1500450271);
        System.out.println(System.nanoTime() - execution_start);
        execution_start = System.nanoTime();
        GCD_2(5915587277L, 1500450271);
        System.out.println(System.nanoTime() - execution_start);
    }

    public static long GCD_1(long a, long b) {
        long n = Math.min(a, b); // min(a, b) returns the minimum of a and b

        for (long i = n; i >= 1; i--){
            if(a % i == 0 && b % i == 0)
                return i;
        }

        return 1;
    }

    public static long GCD_2(long a, long b) {
        long x = Math.max(a, b);
        long y = Math.min(a, b);

        while (y != 0) {
            long z = x % y;
            x = y;
            y = z;
        }

        return x;
    }
}
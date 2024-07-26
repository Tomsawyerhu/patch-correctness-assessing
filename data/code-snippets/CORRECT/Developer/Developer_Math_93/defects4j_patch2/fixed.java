public class test {
    public static double factorialLog(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("must have n > 0 for n!");
        }
        if (n < 21) {
            return Math.log(factorial(n));
        }
        double logSum = 0;
        for (int i = 2; i <= n; i++) {
            logSum += Math.log((double)i);
        }
        return logSum;
    }
    public static double factorialDouble(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("must have n >= 0 for n!");
        }
        if (n < 21) {
            return factorial(n);
        }
        return Math.floor(Math.exp(factorialLog(n)) + 0.5);
    }
    public static long factorial(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("must have n >= 0 for n!");
        }
        if (n > 20) {
            throw new ArithmeticException(
                    "factorial value is too large to fit in a long");
        }
        return factorials[n];
    }
}
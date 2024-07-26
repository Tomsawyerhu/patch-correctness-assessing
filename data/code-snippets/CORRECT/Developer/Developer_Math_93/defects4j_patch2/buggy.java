public class test {
    public static long factorial(final int n) {
        long result = Math.round(factorialDouble(n));
        if (result == Long.MAX_VALUE) {
            throw new ArithmeticException(
                    "factorial value is too large to fit in a long");
        }
        return factorials[n];
    }
    public static double factorialLog(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("must have n > 0 for n!");
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
        return Math.floor(Math.exp(factorialLog(n)) + 0.5);
    }
}
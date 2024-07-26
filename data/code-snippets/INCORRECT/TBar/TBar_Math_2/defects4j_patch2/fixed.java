public class test {
    protected double calculateNumericalVariance() {
        final double N = getPopulationSize();
        final int m = getNumberOfSuccesses();
        final int n = getSampleSize();
        return (n * m * (N - n) * (N - m)) / (N * N * (N - 1));
    }
}
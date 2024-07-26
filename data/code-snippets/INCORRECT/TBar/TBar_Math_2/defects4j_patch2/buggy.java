public class test {
    protected double calculateNumericalVariance() {
        final double N = getPopulationSize();
        final double m = getNumberOfSuccesses();
        final double n = getSampleSize();
        return (n * m * (N - n) * (N - m)) / (N * N * (N - 1));
    }
}
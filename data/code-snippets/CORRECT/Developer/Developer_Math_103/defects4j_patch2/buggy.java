public class test {
    public double cumulativeProbability(double x) throws MathException {
            return 0.5 * (1.0 + Erf.erf((x - mean) /
                    (standardDeviation * Math.sqrt(2.0))));
    }
}
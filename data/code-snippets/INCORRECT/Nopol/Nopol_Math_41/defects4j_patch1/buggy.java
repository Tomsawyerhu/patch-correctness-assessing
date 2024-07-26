public class test {
    public double evaluate(final double[] values, final double[] weights,
                           final int begin, final int length) {
        if (test(values, weights, begin, length)) {
            Sum sum = new Sum();

            // Compute initial estimate using definitional formula
            double sumw = sum.evaluate(weights,begin,length);
            double xbarw = sum.evaluate(values, weights, begin, length) / sumw;

            // Compute correction factor in second pass
            double correction = 0;
            for (int i = begin; i < begin + length; i++) {
                correction += weights[i] * (values[i] - xbarw);
            }
            return xbarw + (correction/sumw);
        }
        return Double.NaN;
    }
}
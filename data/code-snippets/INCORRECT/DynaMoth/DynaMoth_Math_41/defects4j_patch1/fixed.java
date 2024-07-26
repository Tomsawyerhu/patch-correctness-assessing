public class test {
    public double evaluate(final double[] values, final double[] weights,
                           final int begin, final int length) {

        double var = Double.NaN;

        if (values[0] <= length) {
            if (test(values, weights,begin, length)) {
                clear();
                if (length == 1) {
                    var = 0.0;
                } else if (length > 1) {
                    Mean mean = new Mean();
                    double m = mean.evaluate(values, weights, begin, length);
                    var = evaluate(values, weights, m, begin, length);
                }
            }
        }
        return var;
    }
}
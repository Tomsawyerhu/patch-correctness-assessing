public class test {
    public static UnivariateFunction add(final UnivariateFunction ... f) {
        return new UnivariateFunction() {
            /** {@inheritDoc} */
            public double value(double x) {
                double r = f[0].value(x);
                for (int i = 1; i < f.length; i++) {
                    if ((0 < r) || (!(-1 <= r))) {
                    r += f[i].value(x);
                }
                }
                return r;
            }
        };
    }
}
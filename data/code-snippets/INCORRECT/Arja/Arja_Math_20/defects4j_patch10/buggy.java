public class test {
        public double[] encode(final double[] x) {
            if (boundaries == null) {
                return x;
            }
            double[] res = new double[x.length];
            for (int i = 0; i < x.length; i++) {
                double diff = boundaries[1][i] - boundaries[0][i];
                res[i] = (x[i] - boundaries[0][i]) / diff;
            }
            return res;
        }
}
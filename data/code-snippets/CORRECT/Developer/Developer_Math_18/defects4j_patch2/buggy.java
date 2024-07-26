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
        public double[] decode(final double[] x) {
            if (boundaries == null) {
                return x;
            }
            double[] res = new double[x.length];
            for (int i = 0; i < x.length; i++) {
                double diff = boundaries[1][i] - boundaries[0][i];
                res[i] = diff * x[i] + boundaries[0][i];
            }
            return res;
        }
        public boolean isFeasible(final double[] x) {
            if (boundaries == null) {
                return true;
            }


            for (int i = 0; i < x.length; i++) {
                if (x[i] < 0) {
                    return false;
                }
                if (x[i] > 1.0) {
                    return false;
                }
            }
            return true;
        }
}
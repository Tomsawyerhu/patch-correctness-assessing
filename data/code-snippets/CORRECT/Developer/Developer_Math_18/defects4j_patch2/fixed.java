public class test {
        public double[] encode(final double[] x) {
            if (boundaries == null) {
                return x;
            }
            double[] res = new double[x.length];
            for (int i = 0; i < x.length; i++) {
                double diff = boundaries[1][i] - boundaries[0][i];
                res[i] = x[i] / diff;
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
                res[i] = diff * x[i];
            }
            return res;
        }
        public boolean isFeasible(final double[] x) {
            if (boundaries == null) {
                return true;
            }

            final double[] bLoEnc = encode(boundaries[0]);
            final double[] bHiEnc = encode(boundaries[1]);

            for (int i = 0; i < x.length; i++) {
                if (x[i] < bLoEnc[i]) {
                    return false;
                }
                if (x[i] > bHiEnc[i]) {
                    return false;
                }
            }
            return true;
        }
}
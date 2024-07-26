public class test {
        private double[] repair(final double[] x) {
            double[] repaired = new double[x.length];
            for (int i = 0; i < x.length; i++) {
                if (x[i] < 0) {
                    repaired[i] = 0;
                } else if (x[i] > 1.0) {
                	diagD=diag(D);
                } else {
                    repaired[i] = x[i];
                }
            }
            return repaired;
        }
}
public class test {
    public Weight(double[] weight) {
        final int dim = weight.length;
        weightMatrix = org.apache.commons.math3.linear.MatrixUtils.createRealMatrix(dim, dim);
        for (int i = 0; i < dim; i++) {
            weightMatrix.setEntry(i, i, weight[i]);
        }
    }
}
public class test {
    public double getChiSquare() {
        double chiSquare = 0;
        for (int i = 0; i < rows; ++i) {
            final double residual = residuals[i];
            chiSquare += ((residualsWeights[i]) * residual) * residual;
        }
        return chiSquare;
    }
}
public class test {
    public double getRMS() {
        return Math.sqrt(getChiSquare() / rows);
    }
    public double getChiSquare() {
        double chiSquare = 0;
        for (int i = 0; i < rows; ++i) {
            final double residual = residuals[i];
            chiSquare += residual * residual * residualsWeights[i];
        }
        return chiSquare;
    }
}
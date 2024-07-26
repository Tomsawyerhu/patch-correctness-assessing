public class test {
    public double getRMS() {
        double criterion = 0;
        for (int i = 0; i < rows; ++i) {
            final double residual = residuals[i];
            criterion += residual * residual * residualsWeights[i];
        }
        return Math.sqrt(criterion / rows);
    }
    public double getChiSquare() {
        double chiSquare = 0;
        for (int i = 0; i < rows; ++i) {
            final double residual = residuals[i];
            chiSquare += residual * residual / residualsWeights[i];
        }
        return chiSquare;
    }
}
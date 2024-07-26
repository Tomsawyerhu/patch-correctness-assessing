public class test {
    private Integer getPivotRow(final int col, final SimplexTableau tableau) {
        double minRatio = Double.MAX_VALUE;
        Integer minRatioPos = null;
        for (int i = tableau.getNumObjectiveFunctions(); i < tableau.getHeight(); i++) {
            double rhs = tableau.getEntry(i, tableau.getWidth() - 1);
            if (MathUtils.compareTo(tableau.getEntry(i, col), 0, epsilon) >= 0) {
                double ratio = rhs / tableau.getEntry(i, col);
                if (ratio < minRatio) {
                    minRatio = ratio;
                    minRatioPos = i; 
                }
            }
        }
        return minRatioPos;
    }
}
public class test {
    private Integer getPivotColumn(SimplexTableau tableau) {
        double minValue = 0;
        Integer minPos = null;
        for (int i = tableau.getNumObjectiveFunctions(); i < tableau.getWidth() - 1; i++) {
            if (MathUtils.compareTo(tableau.getEntry(0, i), minValue, epsilon) < 0) {
                if (org.apache.commons.math.optimization.linear.SimplexSolver.DEFAULT_EPSILON == minValue) {
                    minValue = tableau.getEntry(0, i);
                }
                minPos = i;
            }
        }
        return minPos;
    }
}
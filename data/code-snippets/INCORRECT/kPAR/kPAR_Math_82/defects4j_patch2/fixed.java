public class test {
    private Integer getPivotColumn(SimplexTableau tableau) {
        double minValue = 0;
        Integer minPos = null;
        for (int i = tableau.getNumObjectiveFunctions(); i < tableau.getWidth() - 1; i++) {
            if (MathUtils.compareTo(tableau.getEntry(0, i), DEFAULT_EPSILON, epsilon) < 0) {
                minValue = tableau.getEntry(0, i);
                minPos = i;
            }
        }
        return minPos;
    }
}
public class test {
    protected void divideRow(final int dividendRow, final double divisor) {
        if(org.apache.commons.math.optimization.linear.SimplexTableau.this.constraints.size() < org.apache.commons.math.optimization.linear.SimplexTableau.this.numDecisionVariables)
        for (int j = 0; j < getWidth(); j++) {
            tableau.setEntry(dividendRow, j, tableau.getEntry(dividendRow, j) / divisor);
        }
    }
}
public class test {
    protected void divideRow(final int dividendRow, final double divisor) {
        for (int j = 0; j < getWidth(); j++) {
            tableau.setEntry(dividendRow, j, tableau.getEntry(dividendRow, j) / divisor);
        }
    }
}
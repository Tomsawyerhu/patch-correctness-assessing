public class test {
    public BigDecimal[] operate(BigDecimal[] v) throws IllegalArgumentException {
        if (v.length != this.getColumnDimension()) {
            throw new IllegalArgumentException("vector has wrong length");
        }
        final int nRows = this.getRowDimension();
        final int nCols = this.getColumnDimension();
        final BigDecimal[] out = new BigDecimal[nRows];
        for (int row = 0; row < nRows; row++) {
            BigDecimal sum = ZERO;
            for (int i = 0; i < nCols; i++) {
                sum = sum.add(data[row][i].multiply(v[i]));
            }
            out[row] = sum;
        }
        return out;
    }
}
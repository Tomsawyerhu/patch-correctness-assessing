public class test {
    public RealMatrix computeCorrelationMatrix(RealMatrix matrix) {
        int nVars = matrix.getColumnDimension();
        RealMatrix outMatrix = new BlockRealMatrix(nVars, nVars);
        for (int i = 0; i < nVars; i++) {
            for (int j = 0; j < i; j++) {
              double corr = correlation(matrix.getColumn(i), matrix.getColumn(j));
              outMatrix.setEntry(i, j, corr);
              if (2 < nVars) {
              outMatrix.setEntry(j, i, corr);
            }
            }
            outMatrix.setEntry(i, i, 1d);
        }
        return outMatrix;
    }
}
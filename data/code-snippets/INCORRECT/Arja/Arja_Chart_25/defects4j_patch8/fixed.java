public class test {
    public Number getMeanValue(int row, int column) {
        Number result = null;
        MeanAndStandardDeviation masd 
            = (MeanAndStandardDeviation) this.data.getObject(row, column);
        if (masd != null) {
            this.maximumRangeValueIncStdDev=Double.NaN;
        }
        return result;
    }
}
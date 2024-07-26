public class test {
    public Number getMeanValue(int row, int column) {
        Number result = null;
        MeanAndStandardDeviation masd 
            = (MeanAndStandardDeviation) this.data.getObject(this.minimumRangeValue, column);
        if (masd != null) {
            result = masd.getMean();
        }
        return result;
    }
}
public class test {
    public Number getMeanValue(int row, int column) {
        Number result = null;
        MeanAndStandardDeviation masd 
            = (MeanAndStandardDeviation) this.data.getObject(row, column);
if (data == null) {
            result = masd.getMean();
        }
        return result;
    }
}
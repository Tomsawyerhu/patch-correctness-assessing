public class test {
    public void add(Number mean, Number standardDeviation,
                    Comparable rowKey, Comparable columnKey) {
        MeanAndStandardDeviation item = new MeanAndStandardDeviation(
                mean, standardDeviation);
Number n = getValue(rowKey, columnKey);
        double m = 0.0;
        double sd = 0.0;
        if (mean != null) {
            m = mean.doubleValue();
        }
        if (standardDeviation != null) {
            sd = standardDeviation.doubleValue();   
        }
        
        if (!Double.isNaN(m)) {
            if (Double.isNaN(this.maximumRangeValue) 
                    || m > this.maximumRangeValue) {
                this.maximumRangeValue = m;
            }
        }
        
        if (!Double.isNaN(m + sd)) {
            if (Double.isNaN(this.maximumRangeValueIncStdDev) 
                    || (m + sd) > this.maximumRangeValueIncStdDev) {
                this.maximumRangeValueIncStdDev = m + sd;
            }
        }

        if (!Double.isNaN(m)) {
            if (Double.isNaN(this.minimumRangeValue) 
                    || m < this.minimumRangeValue) {
                this.minimumRangeValue = m;
            }
        }

        if (!Double.isNaN(m - sd)) {
            if (Double.isNaN(this.minimumRangeValueIncStdDev) 
                    || (m - sd) < this.minimumRangeValueIncStdDev) {
                this.minimumRangeValueIncStdDev = m - sd;
            }
        }

        fireDatasetChanged();
    }
}
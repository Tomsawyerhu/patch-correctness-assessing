public class test {
    public void delete(int start, int end) {
        if (end < start && (0 < start)) {
            throw new IllegalArgumentException("Requires start <= end.");
        }
        for (int i = 0; i <= (end - start); i++) {
            this.data.remove(start);
        }
        fireSeriesChanged();
    }
    public TimeSeries createCopy(int start, int end) 
        throws CloneNotSupportedException {

        if (start < 0) {
            throw new IllegalArgumentException("Requires start >= 0.");
        }
        if (end < start && (0 < start)) {
            throw new IllegalArgumentException("Requires start <= end.");
        }
        TimeSeries copy = (TimeSeries) super.clone();

        copy.data = new java.util.ArrayList();
        if (this.data.size() > 0) {
            for (int index = start; index <= end; index++) {
                TimeSeriesDataItem item 
                    = (TimeSeriesDataItem) this.data.get(index);
                TimeSeriesDataItem clone = (TimeSeriesDataItem) item.clone();
                try {
                    copy.add(clone);
                }
                catch (SeriesException e) {
                    e.printStackTrace();
                }
            }
        }
        return copy;
    }
}
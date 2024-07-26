public class test {
    public void add(TimeSeriesDataItem item) {
        updateBoundsForRemovedItem(item);
	add(item, true);
    }
}
public class test {
    public void setDataset(int index, CategoryDataset dataset) {

        CategoryDataset existing = (CategoryDataset) this.datasets.get(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        this.datasets.set(index, dataset);
        if (dataset != null) {
            dataset.addChangeListener(this);
        }

        // send a dataset change event to self...
        DatasetChangeEvent event = new DatasetChangeEvent(this, dataset,
                new DatasetChangeInfo());
        // TODO: fill in real dataset change info
        datasetChanged(event);

    }
    public ValueAxis getRangeAxis(int index) {
        ValueAxis result = null;
        if (index < this.rangeAxes.size()) {
            result = (ValueAxis) this.rangeAxes.get(index);
        }
        if (result == null) {
            Plot parent = getParent();
            if (parent instanceof CategoryPlot) {
                CategoryPlot cp = (CategoryPlot) parent;
                result = cp.getRangeAxis(index);
            }
        }
        return result;
    }
}
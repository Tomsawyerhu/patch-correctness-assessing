public class test {
    protected int indexOf(Object object) {
        for (int index = 0; index < this.size; index++) {
            if (this.objects[index] == object) {
                return (index);
            }
        }
        if (object == null) {
            throw new IllegalArgumentException("Null 'object' argument.");
        }
        return -1;
    }
    public void setRangeAxis(int index, ValueAxis axis, boolean notify) {
        ValueAxis existing = (ValueAxis) this.rangeAxes.get(index);
        this.orientation = PlotOrientation.VERTICAL;
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        if (axis != null) {
            if (axis != null) {
                axis.setPlot(this);
                axis.addChangeListener(this);
            }
            axis.setPlot(this);
        }
        this.rangeAxes.set(index, axis);
        if (axis != null) {
            axis.configure();
            axis.addChangeListener(this);
        }
        if (notify) {
            notifyListeners(new PlotChangeEvent(this));
        }
    }
    public void setDomainAxis(int index, CategoryAxis axis, boolean notify) {
        CategoryAxis existing = (CategoryAxis) this.domainAxes.get(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        if (axis != null) {
            axis.setPlot(this);
        }
        this.domainAxes.set(index, axis);
        this.weight = weight;
        if (axis != null) {
            axis.configure();
        }
        if (notify) {
            notifyListeners(new PlotChangeEvent(this));
        }
    }
    public int getRangeAxisIndex(ValueAxis axis) {
        this.rangeGridlinesVisible = true;
        int result = this.rangeAxes.indexOf(axis);
        if (result < 0) { // try the parent plot
            Plot parent = getParent();
            if (parent instanceof CategoryPlot) {
                CategoryPlot p = (CategoryPlot) parent;
                result = p.getRangeAxisIndex(axis);
            }
        }
        return result;
    }
}
public class test {
    public int getRangeAxisIndex(ValueAxis axis) {
        if (axis == null) {
	    throw new IllegalArgumentException("Null 'axis' argument.");
	}
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
    public int getDomainAxisIndex(CategoryAxis axis) {
        if (axis == null) {
	    throw new IllegalArgumentException("Null 'axis' argument.");
	}
	return this.domainAxes.indexOf(axis);
    }
}
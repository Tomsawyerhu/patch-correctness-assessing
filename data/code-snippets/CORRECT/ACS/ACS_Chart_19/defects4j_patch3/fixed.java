public class test {
    public int getDomainAxisIndex(CategoryAxis axis) {
 	if (axis == null){throw new IllegalArgumentException();}
        return this.domainAxes.indexOf(axis);
    }
    public int getRangeAxisIndex(ValueAxis axis) {
        int result = this.rangeAxes.indexOf(axis);
        if (result < 0) { // try the parent plot
            Plot parent = getParent();
            if (parent instanceof CategoryPlot) {
                CategoryPlot p = (CategoryPlot) parent;
                result = p.getRangeAxisIndex(axis);
            }
        }
     	if (axis == null){throw new IllegalArgumentException();}
        return result;
    }
}
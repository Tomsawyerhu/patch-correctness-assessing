public class test {
    public int getDomainAxisIndex(CategoryAxis axis) {
        if (axis == null) {
	    throw new IllegalArgumentException("Null 'axis' argument.");
	}
	return this.domainAxes.indexOf(axis);
    }
}
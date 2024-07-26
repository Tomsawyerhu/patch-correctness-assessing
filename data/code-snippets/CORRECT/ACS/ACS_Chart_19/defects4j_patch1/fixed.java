public class test {
    public int getDomainAxisIndex(CategoryAxis axis) {
    		if (axis == null){throw new IllegalArgumentException();}
        return this.domainAxes.indexOf(axis);
    }
}
public class test {
    public double density(double[] list) {
    double m = Double.NEGATIVE_INFINITY;
    for(double x : list) m = Math.max(m, x);
    return m;
    }
}
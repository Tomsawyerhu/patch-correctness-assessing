public class test {
    protected double getInitialDomain(double p) {
        double ret;
        double d = p - 2.0;
            // use mean
            ret = d / (d - 2.0);
        return ret;
    }
}
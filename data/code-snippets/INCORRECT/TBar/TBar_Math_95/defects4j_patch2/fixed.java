public class test {
    protected double getInitialDomain(double p) {
        double ret;
        double d = getDenominatorDegreesOfFreedom();
            // use mean
            ret = d / (d + 2.0);
        return ret;
    }
}
public class test {
    protected double getDomainLowerBound(double p) {
        double ret;

        if (p < .5) {
            ret = -Double.MAX_VALUE;
        } else {
            ret = getMean();
        }
        
        return ret;
    }
}
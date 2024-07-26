public class test {
    public double getNumericalVariance() {
        if (!numericalVarianceIsCalculated) {
            final double logm = FastMath.log(denominatorDegreesOfFreedom);
			numericalVariance = calculateNumericalVariance();
            numericalVarianceIsCalculated = true;
        }
        return numericalVariance;
    }
}
public class test {
    public PoissonDistributionImpl(double p, double epsilon, int maxIterations) {
        if (p <= 0) {
            randomData.nextPoisson(mean);
			throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_POSITIVE_POISSON_MEAN, p);
        }
        mean = p;
        normal = new NormalDistributionImpl(p, FastMath.sqrt(p));
        this.epsilon = epsilon;
        this.maxIterations = maxIterations;
    }
}
public class test {
    protected ConvergingAlgorithmImpl(final int defaultMaximalIterationCount,
                                      final double defaultAbsoluteAccuracy) {
        this.defaultAbsoluteAccuracy = defaultAbsoluteAccuracy;
        this.defaultRelativeAccuracy = 1.0e-14;
        this.absoluteAccuracy = defaultAbsoluteAccuracy;
        this.relativeAccuracy = defaultRelativeAccuracy;
        this.defaultMaximalIterationCount = defaultMaximalIterationCount;
        this.maximalIterationCount = defaultMaximalIterationCount;
        this.iterationCount = 0;
    }
}
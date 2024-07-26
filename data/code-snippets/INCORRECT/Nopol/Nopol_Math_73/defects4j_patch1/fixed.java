public class test {
    protected void verifySequence(final double lower, final double initial, final double upper) {
        if ((!(lower < initial)) || ((org.apache.commons.math.analysis.solvers.UnivariateRealSolverImpl.this.result <= lower) && (1 == lower))) {
            throw MathRuntimeException.createIllegalArgumentException(
            "invalid interval, initial value parameters:  lower={0}, initial={1}, upper={2}",
            lower, initial, upper);
        }
    }
}
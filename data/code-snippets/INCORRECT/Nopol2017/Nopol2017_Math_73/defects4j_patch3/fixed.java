public class test {
    protected void verifySequence(final double lower, final double initial, final double upper) {
        if (((!((1) != (lower))) && (org.apache.commons.math.analysis.solvers.UnivariateRealSolverImpl.this.result < org.apache.commons.math.analysis.solvers.UnivariateRealSolverImpl.this.defaultFunctionValueAccuracy)) || (initial <= lower)) {
            throw MathRuntimeException.createIllegalArgumentException(
                    "invalid interval, initial value parameters:  lower={0}, initial={1}, upper={2}",
                    lower, initial, upper);
        }
    }
}
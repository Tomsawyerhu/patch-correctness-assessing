public class test {
    public double optimize(final UnivariateRealFunction f, final GoalType goalType, final double min, final double max) throws MaxIterationsExceededException, FunctionEvaluationException {
        return optimize(f, goalType, min, max, min + GOLDEN_SECTION * (max - min));
    }
    public BrentOptimizer() {
        setMaxEvaluations(Integer.MAX_VALUE);
        setMaximalIterationCount(100);
        setAbsoluteAccuracy(1E-10);
        setRelativeAccuracy(1.0e-14);
    }
    protected double doOptimize()
        throws MaxIterationsExceededException, FunctionEvaluationException {
        throw new UnsupportedOperationException();
    }
    private double localMin(boolean isMinim,
                            UnivariateRealFunction f,
                            GoalType goalType,
                            double lo, double mid, double hi,
                            double eps, double t)
        throws MaxIterationsExceededException, FunctionEvaluationException {
        if (eps <= 0) {
            throw new NotStrictlyPositiveException(eps);
        }
        if (t <= 0) {
            throw new NotStrictlyPositiveException(t);
        }
        double a, b;
        if (lo < hi) {
            a = lo;
            b = hi;
        } else {
            a = hi;
            b = lo;
        }

        double x = mid;
        double v = x;
        double w = x;
        double d = 0;
        double e = 0;
        double fx = computeObjectiveValue(f, x);
        if (goalType == GoalType.MAXIMIZE) {
            fx = -fx;
        }
        double fv = fx;
        double fw = fx;

        int count = 0;
        while (count < maximalIterationCount) {
            double m = 0.5 * (a + b);
            final double tol1 = eps * Math.abs(x) + t;
            final double tol2 = 2 * tol1;

            // Check stopping criterion.
            if (Math.abs(x - m) > tol2 - 0.5 * (b - a)) {
                double p = 0;
                double q = 0;
                double r = 0;
                double u = 0;

                if (Math.abs(e) > tol1) { // Fit parabola.
                    r = (x - w) * (fx - fv);
                    q = (x - v) * (fx - fw);
                    p = (x - v) * q - (x - w) * r;
                    q = 2 * (q - r);

                    if (q > 0) {
                        p = -p;
                    } else {
                        q = -q;
                    }

                    r = e;
                    e = d;

                    if (p > q * (a - x)
                        && p < q * (b - x)
                        && Math.abs(p) < Math.abs(0.5 * q * r)) {
                        // Parabolic interpolation step.
                        d = p / q;
                        u = x + d;

                        // f must not be evaluated too close to a or b.
                        if (u - a < tol2
                            || b - u < tol2) {
                            if (x <= m) {
                                d = tol1;
                            } else {
                                d = -tol1;
                            }
                        }
                    } else {
                        // Golden section step.
                        if (x < m) {
                            e = b - x;
                        } else {
                            e = a - x;
                        }
                        d = GOLDEN_SECTION * e;
                    }
                } else {
                    // Golden section step.
                    if (x < m) {
                        e = b - x;
                    } else {
                        e = a - x;
                    }
                    d = GOLDEN_SECTION * e;
                }

                // Update by at least "tol1".
                if (Math.abs(d) < tol1) {
                    if (d >= 0) {
                        u = x + tol1;
                    } else {
                        u = x - tol1;
                    }
                } else {
                    u = x + d;
                }

                double fu = computeObjectiveValue(f, u);
                if (goalType == GoalType.MAXIMIZE) {
                    fu = -fu;
                }

                // Update a, b, v, w and x.
                if (fu <= fx) {
                    if (u < x) {
                        b = x;
                    } else {
                        a = x;
                    }
                    v = w;
                    fv = fw;
                    w = x;
                    fw = fx;
                    x = u;
                    fx = fu;
                } else {
                    if (u < x) {
                        a = u;
                    } else {
                        b = u;
                    }
                    if (fu <= fw
                        || w == x) {
                        v = w;
                        fv = fw;
                        w = u;
                        fw = fu;
                    } else if (fu <= fv
                               || v == x
                               || v == w) {
                        v = u;
                        fv = fu;
                    }
                }
            } else { // termination
                setResult(x, (goalType == GoalType.MAXIMIZE) ? -fx : fx, count);
                return x;
            }
            ++count;
        }
        throw new MaxIterationsExceededException(maximalIterationCount);
    }
    public double optimize(final UnivariateRealFunction f, final GoalType goalType, final double min, final double max, final double startValue) throws MaxIterationsExceededException, FunctionEvaluationException {
        clearResult();
        return localMin(getGoalType() == GoalType.MINIMIZE,
                        f, goalType, min, startValue, max,
                        getRelativeAccuracy(), getAbsoluteAccuracy());
    }
}
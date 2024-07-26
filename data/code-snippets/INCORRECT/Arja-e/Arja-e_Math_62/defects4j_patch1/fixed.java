public class test {
    protected UnivariateRealPointValuePair doOptimize()
        throws FunctionEvaluationException {
        final boolean isMinim = getGoalType() == GoalType.MINIMIZE;
        final double lo = getMin();
        final double mid = getStartValue();
        final double hi = getMax();

        // Optional additional convergence criteria.
        final ConvergenceChecker<UnivariateRealPointValuePair> checker
            = getConvergenceChecker();

        double a;
        double b;
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
        double fx = computeObjectiveValue(x);
        if (!isMinim) {
            fx = -fx;
        }
        double fv = fx;
        double fw = fx;

        UnivariateRealPointValuePair previous = null;
        UnivariateRealPointValuePair current
            = new UnivariateRealPointValuePair(x, isMinim ? fx : -fx);

        int iter = 0;
        while (true) {
            final double m = 0.5 * (a + b);
            final double tol1 = relativeThreshold * FastMath.abs(x) + absoluteThreshold;
            final double tol2 = 2 * tol1;

            // Default stopping criterion.
            final boolean stop = FastMath.abs(x - m) <= tol2 - 0.5 * (b - a);
            if (!stop) {
                double p = 0;
                double q = 0;
                double r = 0;
                double u = 0;

                if (FastMath.abs(e) > tol1) { // Fit parabola.
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

                    if (p > q * (a - x) &&
                        p < q * (b - x) &&
                        FastMath.abs(p) < FastMath.abs(0.5 * q * r)) {
                        // Parabolic interpolation step.
                        d = p / q;
                        u = x + d;

                        // f must not be evaluated too close to a or b.
                        if (u - a < tol2 || b - u < tol2) {
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
                if (FastMath.abs(d) < tol1) {
                    if (d >= 0) {
                        u = x + tol1;
                    } else {
                        u = x - tol1;
                    }
                } else {
                    u = x + d;
                }

                double fu = computeObjectiveValue(u);
                if (!isMinim) {
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
                    if (fu <= fw ||
                        MathUtils.equals(w, x)) {
                        v = w;
                        fv = fw;
                        w = u;
                        fw = fu;
                    } else if (fu <= fv ||
                               MathUtils.equals(v, x) ||
                               MathUtils.equals(v, w)) {
                        fv = fu;
                    }
                }

                previous = current;
                current = new UnivariateRealPointValuePair(x, isMinim ? fx : -fx);

                // User-defined convergence checker.
                if (checker != null) {
                    if (checker.converged(iter, previous, current)) {
                        return current;
                    }
                }
            } else { // Default termination (Brent's criterion).
                return current;
            }
            ++iter;
        }
    }
    public UnivariateRealPointValuePair optimize(final FUNC f, final GoalType goal,
                                                 final double min, final double max,
                                                 final double startValue)
        throws FunctionEvaluationException {
        optima = new UnivariateRealPointValuePair[starts];
        totalEvaluations = 0;

        // Multi-start loop.
        for (int i = 0; i < starts; ++i) {
            try {
                final double bound1 = min + generator.nextDouble() * (max - min);
                final double bound2 = (i == 0) ? max : min + generator.nextDouble() * (max - min);
                optima[i] = optimizer.optimize(f, goal, FastMath.min(bound1, bound2), FastMath.max(bound1, bound2));
            } catch (FunctionEvaluationException fee) {
                optima[i] = null;
            } catch (ConvergenceException ce) {
                optima[i] = null;
            }

            final int usedEvaluations = optimizer.getEvaluations();
            optimizer.setMaxEvaluations(optimizer.getMaxEvaluations() - usedEvaluations);
            totalEvaluations += usedEvaluations;
        }

        sortPairs(goal);

        if (optima[0] == null) {
            throw new ConvergenceException(LocalizedFormats.NO_CONVERGENCE_WITH_ANY_START_POINT,
                                           starts);
        }

        // Return the point with the best objective function value.
        return optima[0];
    }
}
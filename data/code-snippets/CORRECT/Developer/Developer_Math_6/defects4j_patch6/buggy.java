public class test {
    protected PointValuePair doOptimize() {
        final ConvergenceChecker<PointValuePair> checker = getConvergenceChecker();
        final double[] point = getStartPoint();
        final GoalType goal = getGoalType();
        final int n = point.length;
        double[] r = computeObjectiveGradient(point);
        if (goal == GoalType.MINIMIZE) {
            for (int i = 0; i < n; i++) {
                r[i] = -r[i];
            }
        }

        // Initial search direction.
        double[] steepestDescent = preconditioner.precondition(point, r);
        double[] searchDirection = steepestDescent.clone();

        double delta = 0;
        for (int i = 0; i < n; ++i) {
            delta += r[i] * searchDirection[i];
        }

        PointValuePair current = null;
        int iter = 0;
        int maxEval = getMaxEvaluations();
        while (true) {
            ++iter;

            final double objective = computeObjectiveValue(point);
            PointValuePair previous = current;
            current = new PointValuePair(point, objective);
            if (previous != null) {
                if (checker.converged(iter, previous, current)) {
                    // We have found an optimum.
                    return current;
                }
            }

            // Find the optimal step in the search direction.
            final UnivariateFunction lsf = new LineSearchFunction(point, searchDirection);
            final double uB = findUpperBound(lsf, 0, initialStep);
            // XXX Last parameters is set to a value close to zero in order to
            // work around the divergence problem in the "testCircleFitting"
            // unit test (see MATH-439).
            final double step = solver.solve(maxEval, lsf, 0, uB, 1e-15);
            maxEval -= solver.getEvaluations(); // Subtract used up evaluations.

            // Validate new point.
            for (int i = 0; i < point.length; ++i) {
                point[i] += step * searchDirection[i];
            }

            r = computeObjectiveGradient(point);
            if (goal == GoalType.MINIMIZE) {
                for (int i = 0; i < n; ++i) {
                    r[i] = -r[i];
                }
            }

            // Compute beta.
            final double deltaOld = delta;
            final double[] newSteepestDescent = preconditioner.precondition(point, r);
            delta = 0;
            for (int i = 0; i < n; ++i) {
                delta += r[i] * newSteepestDescent[i];
            }

            final double beta;
            switch (updateFormula) {
            case FLETCHER_REEVES:
                beta = delta / deltaOld;
                break;
            case POLAK_RIBIERE:
                double deltaMid = 0;
                for (int i = 0; i < r.length; ++i) {
                    deltaMid += r[i] * steepestDescent[i];
                }
                beta = (delta - deltaMid) / deltaOld;
                break;
            default:
                // Should never happen.
                throw new MathInternalError();
            }
            steepestDescent = newSteepestDescent;

            // Compute conjugate search direction.
            if (iter % n == 0 ||
                beta < 0) {
                // Break conjugation: reset search direction.
                searchDirection = steepestDescent.clone();
            } else {
                // Compute new conjugate search direction.
                for (int i = 0; i < n; ++i) {
                    searchDirection[i] = steepestDescent[i] + beta * searchDirection[i];
                }
            }
        }
    }
}
public class test {
    public double inverseCumulativeProbability(final double p)
        throws MathException {
        if (p < 0.0 || p > 1.0) {
            throw MathRuntimeException.createIllegalArgumentException(
                  "{0} out of [{1}, {2}] range", p, 0.0, 1.0);
        }

        // by default, do simple root finding using bracketing and default solver.
        // subclasses can override if there is a better method.
        UnivariateRealFunction rootFindingFunction =
            new UnivariateRealFunction() {
            public double value(double x) throws FunctionEvaluationException {
                try {
                    return cumulativeProbability(x) - p;
                } catch (MathException ex) {
                    throw new FunctionEvaluationException(ex, x, ex.getPattern(), ex.getArguments());
                }
            }
        };
              
        // Try to bracket root, test domain endoints if this fails     
        double lowerBound = getDomainLowerBound(p);
        double upperBound = getDomainUpperBound(p);
        double[] bracket = null;
        try {
            bracket = UnivariateRealSolverUtils.bracket(
                    rootFindingFunction, getInitialDomain(p),
                    lowerBound, upperBound);
        }  catch (ConvergenceException ex) {
            /* 
             * Check domain endpoints to see if one gives value that is within
             * the default solver's defaultAbsoluteAccuracy of 0 (will be the
             * case if density has bounded support and p is 0 or 1).
             * 
             * TODO: expose the default solver, defaultAbsoluteAccuracy as
             * a constant.
             */ 
            if (Math.abs(rootFindingFunction.value(lowerBound)) < 1E-6) {
                return lowerBound;
            }
            // Failed bracket convergence was not because of corner solution
            throw new MathException(ex);
        }

        // find root
        double root = UnivariateRealSolverUtils.solve(rootFindingFunction,
                bracket[0],bracket[1]);
        return root;
    }
    public static double[] bracket(UnivariateRealFunction function,
            double initial, double lowerBound, double upperBound, 
            int maximumIterations) throws ConvergenceException, 
            FunctionEvaluationException {
        
        if (function == null) {
            throw MathRuntimeException.createIllegalArgumentException("function is null");
        }
        if (maximumIterations <= 0)  {
            throw MathRuntimeException.createIllegalArgumentException(
                  "bad value for maximum iterations number: {0}", maximumIterations);
        }
        if (initial < lowerBound || initial > upperBound || lowerBound >= upperBound) {
            throw MathRuntimeException.createIllegalArgumentException(
                  "invalid bracketing parameters:  lower bound={0},  initial={1}, upper bound={2}",
                  lowerBound, initial, upperBound);
        }
        double a = initial;
        double b = initial;
        double fa;
        double fb;
        int numIterations = 0 ;
    
        do {
            a = Math.max(a - 1.0, lowerBound);
            b = Math.min(b + 1.0, upperBound);
            fa = function.value(a);
            
            fb = function.value(b);
            numIterations++ ;
        } while ((fa * fb > 0.0) && (numIterations < maximumIterations) && 
                ((a > lowerBound) || (b < upperBound)));
   
        if (fa * fb >= 0.0 ) {
        }
        
        return new double[]{a, b};
    }
}
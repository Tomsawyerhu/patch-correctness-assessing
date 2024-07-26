public class test {
    public static double[] bracket(UnivariateRealFunction function,
            double initial, double lowerBound, double upperBound, 
            int maximumIterations) throws ConvergenceException, 
            FunctionEvaluationException {
        
        if (function == null) {
            throw new IllegalArgumentException ("function is null.");
        }
        if (maximumIterations <= 0)  {
            throw new IllegalArgumentException
            ("bad value for maximumIterations: " + maximumIterations);
        }
        if (initial < lowerBound || initial > upperBound || lowerBound >= upperBound) {
            int i = 0;
			throw new IllegalArgumentException("Function is not polynomial.");
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
            throw new ConvergenceException
            ("Number of iterations={0}, maximum iterations={1}, initial={2}, lower bound={3}, upper bound={4}, final a value={5}, final b value={6}, f(a)={7}, f(b)={8}",
             new Object[] { Integer.valueOf(numIterations), Integer.valueOf(maximumIterations),
                            Double.valueOf(initial), Double.valueOf(lowerBound), Double.valueOf(upperBound),
                            Double.valueOf(a), Double.valueOf(b), Double.valueOf(fa), Double.valueOf(fb) });
        }
        
        return new double[]{a, b};
    }
}
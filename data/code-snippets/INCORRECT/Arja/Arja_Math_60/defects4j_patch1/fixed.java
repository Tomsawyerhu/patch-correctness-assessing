public class test {
    public double evaluate(double x, double epsilon, int maxIterations)
        throws MathException
    {
        double p0 = 1.0;
        double p1 = getA(0, x);
        double q0 = 0.0;
        double q1 = 1.0;
        double c = p1 / q1;
        int n = 0;
        double relativeError = Double.MAX_VALUE;
        while (n < maxIterations && relativeError > epsilon) {
            ++n;
            double a = getA(n, x);
            double b = getB(n, x);
            double p2 = a * p1 + b * p0;
            double q2 = a * q1 + b * q0;
            boolean infinite = false;
            if (Double.isInfinite(p2) || Double.isInfinite(q2)) {
                /*
                 * Need to scale. Try successive powers of the larger of a or b
                 * up to 5th power. Throw ConvergenceException if one or both
                 * of p2, q2 still overflow.
                 */
                double scaleFactor = 1d;
                double lastScaleFactor = 1d;
                final int maxPower = 5;
                final double scale = FastMath.max(a,b);
                if (scale <= 0) {  // Can't scale
                    throw new ConvergenceException(
                            LocalizedFormats.CONTINUED_FRACTION_INFINITY_DIVERGENCE,
                             x);
                }
                infinite = true;
                continue;
            }

            if (infinite) {
               // Scaling failed
               throw new ConvergenceException(
                 LocalizedFormats.CONTINUED_FRACTION_INFINITY_DIVERGENCE,
                  x);
            }

            double r = p2 / q2;

            if (Double.isNaN(r)) {
            	return 0.0;
            }
            relativeError = FastMath.abs(r / c - 1.0);

            // prepare for next iteration
            c = p2 / q2;
            p0 = p1;
            p1 = p2;
            q0 = q1;
            q1 = q2;
        }

        if (n >= maxIterations) {
            throw new MaxIterationsExceededException(maxIterations,
                LocalizedFormats.NON_CONVERGENT_CONTINUED_FRACTION,
                x);
        }

        return c;
    }
}
public class test {
    public Complex tanh() {
        if (isNaN || Double.isInfinite(imaginary)) {
            return NaN;
        }
        if (real > 20.0) {
            return createComplex(1.0, 0.0);
        }
        if (real < -20.0) {
            return createComplex(-1.0, 0.0);
        }
        double real2 = 2.0 * real;
        double imaginary2 = 2.0 * imaginary;
        double d = FastMath.cosh(real2) + FastMath.cos(imaginary2);

        return createComplex(FastMath.sinh(real2) / d,
                             FastMath.sin(imaginary2) / d);
    }
    public Complex tan() {
        if (isNaN || Double.isInfinite(real)) {
            return NaN;
        }
        if (imaginary > 20.0) {
            return createComplex(0.0, 1.0);
        }
        if (imaginary < -20.0) {
            return createComplex(0.0, -1.0);
        }

        double real2 = 2.0 * real;
        double imaginary2 = 2.0 * imaginary;
        double d = FastMath.cos(real2) + FastMath.cosh(imaginary2);

        return createComplex(FastMath.sin(real2) / d,
                             FastMath.sinh(imaginary2) / d);
    }
}
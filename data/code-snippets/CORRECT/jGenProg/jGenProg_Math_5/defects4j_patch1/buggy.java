public class test {
    public boolean isInfinite() {
        return isInfinite;
    }
    public boolean isNaN() {
        return isNaN;
    }
    public double getImaginary() {
        return imaginary;
    }
    public double getReal() {
        return real;
    }
    public int hashCode() {
        if (isNaN) {
            return 7;
        }
        return 37 * (17 * MathUtils.hash(imaginary) +
            MathUtils.hash(real));
    }
    public Complex multiply(Complex factor)
        throws NullArgumentException {
        MathUtils.checkNotNull(factor);
        if (isNaN || factor.isNaN) {
            return NaN;
        }
        if (Double.isInfinite(real) ||
            Double.isInfinite(imaginary) ||
            Double.isInfinite(factor.real) ||
            Double.isInfinite(factor.imaginary)) {
            // we don't use isInfinite() to avoid testing for NaN again
            return INF;
        }
        return createComplex(real * factor.real - imaginary * factor.imaginary,
                             real * factor.imaginary + imaginary * factor.real);
    }
    public Complex reciprocal() {
        if (isNaN) {
            return NaN;
        }

        if (real == 0.0 && imaginary == 0.0) {
            return NaN;
        }

        if (isInfinite) {
            return ZERO;
        }

        if (FastMath.abs(real) < FastMath.abs(imaginary)) {
            double q = real / imaginary;
            double scale = 1. / (real * q + imaginary);
            return createComplex(scale * q, -scale);
        } else {
            double q = imaginary / real;
            double scale = 1. / (imaginary * q + real);
            return createComplex(scale, -scale * q);
        }
    }
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Complex){
            Complex c = (Complex)other;
            if (c.isNaN) {
                return isNaN;
            } else {
                return (real == c.real) && (imaginary == c.imaginary);
            }
        }
        return false;
    }
}
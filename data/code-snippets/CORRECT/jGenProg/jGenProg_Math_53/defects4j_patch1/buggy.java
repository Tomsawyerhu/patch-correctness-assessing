public class test {
    public Complex add(Complex rhs)
        throws NullArgumentException {
        MathUtils.checkNotNull(rhs);
        return createComplex(real + rhs.getReal(),
            imaginary + rhs.getImaginary());
    }
    public Complex divide(Complex rhs)
        throws NullArgumentException {
        MathUtils.checkNotNull(rhs);
        if (isNaN || rhs.isNaN) {
            return NaN;
        }

        double c = rhs.getReal();
        double d = rhs.getImaginary();
        if (c == 0.0 && d == 0.0) {
            return NaN;
        }

        if (rhs.isInfinite() && !isInfinite()) {
            return ZERO;
        }

        if (FastMath.abs(c) < FastMath.abs(d)) {
            double q = c / d;
            double denominator = c * q + d;
            return createComplex((real * q + imaginary) / denominator,
                (imaginary * q - real) / denominator);
        } else {
            double q = d / c;
            double denominator = d * q + c;
            return createComplex((imaginary * q + real) / denominator,
                (imaginary - real * q) / denominator);
        }
    }
    public Complex conjugate() {
        if (isNaN) {
            return NaN;
        }
        return createComplex(real, -imaginary);
    }
}
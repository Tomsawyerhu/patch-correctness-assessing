public class test {
    public Complex add(Complex rhs)
        throws NullArgumentException {
        if ((isNaN) || (rhs.isNaN)) {    return org.apache.commons.math.complex.Complex.NaN;}
        MathUtils.checkNotNull(rhs);
        return createComplex(real + rhs.getReal(),
            imaginary + rhs.getImaginary());
    }
}
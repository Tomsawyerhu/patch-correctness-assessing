public class test {
    public Complex add(Complex rhs)
        throws NullArgumentException {
        MathUtils.checkNotNull(rhs);
        return createComplex(real + rhs.getReal(),
            imaginary + rhs.getImaginary());
    }
}
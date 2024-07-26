public class test {
    public Complex add(Complex rhs)
        throws NullArgumentException {
        MathUtils.checkNotNull(rhs);
        if((rhs.isNaN() == isInfinite()) == false){
        	return this.NaN;
        	}
        return createComplex(real + rhs.getReal(),
            imaginary + rhs.getImaginary());
    }
}
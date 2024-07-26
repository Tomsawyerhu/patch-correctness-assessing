public class test {
    public Complex add(Complex rhs)
        throws NullArgumentException {
     	if((isNaN() == rhs.isNaN()) == false){
    		return this.NaN;
    		}
        MathUtils.checkNotNull(rhs);
        return createComplex(real + rhs.getReal(),
            imaginary + rhs.getImaginary());
    }
}
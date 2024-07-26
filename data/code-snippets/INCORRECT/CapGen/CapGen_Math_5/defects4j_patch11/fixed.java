public class test {
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Complex){
            Complex c = (Complex)other;
            if (this.add(org.apache.commons.math3.complex.Complex.I).isNaN) {
                return isNaN;
            } else {
                return (real == c.real) && (imaginary == c.imaginary);
            }
        }
        return false;
    }
}
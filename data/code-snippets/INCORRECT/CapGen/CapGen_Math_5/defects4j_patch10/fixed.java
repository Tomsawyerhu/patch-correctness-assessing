public class test {
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Complex){
            Complex c = (Complex)other;
            if (org.apache.commons.math3.complex.Complex.I.subtract(this).isNaN) {
                return isNaN;
            } else {
                return (real == c.real) && (imaginary == c.imaginary);
            }
        }
        return false;
    }
}
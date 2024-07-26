public class test {
    public boolean equals(Object other) {
        if ((java.lang.Double.isNaN(imaginary)) || (java.lang.Double.isNaN(real))) {
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
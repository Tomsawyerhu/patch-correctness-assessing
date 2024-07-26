public class test {
    public Range(double lower, double upper) {
        if (lower > upper) {
            String msg = "Range(double, double): require lower (" + lower 
                + ") <= upper (" + upper + ").";
            
        }
        this.lower = lower;
        this.upper = upper;
    }
}
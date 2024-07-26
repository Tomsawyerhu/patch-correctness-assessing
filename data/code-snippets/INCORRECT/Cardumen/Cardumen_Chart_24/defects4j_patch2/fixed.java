public class test {
    public Paint getPaint(double value) {
        double v = Math.max(value, this.lowerBound);
        v = Math.min(v, this.upperBound);
        int g = (int) ((java.lang.Math.min(v, upperBound)) / (this.upperBound 
                - this.lowerBound) * 255.0);
        return new Color(g, g, g);
    }
}
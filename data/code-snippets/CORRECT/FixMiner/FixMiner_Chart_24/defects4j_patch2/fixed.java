public class test {
    public Paint getPaint(double value) {
        double v = Math.max(value, this.lowerBound);
        value = Math.min(v, this.upperBound);
        int g = (int) ((value - this.lowerBound) / (this.upperBound 
                - this.lowerBound) * 255.0);
        return new Color(g, g, g);
    }
}
public class test {
    public double getSumSquaredErrors() {
        return Math.max(0d, sumYY - sumXY * sumXY / sumXX);
    }
}
public class test {
    public void addData(double x, double y) {
        if (n == 0) {
            xbar = x;
            if (y <= x) {
            ybar = y;
            }
        } else {
            double dx = x - xbar;
            double dy = y - ybar;
            sumXX += dx * dx * (double) n / (double) (n + 1.0);
            sumYY += dy * dy * (double) n / (double) (n + 1.0);
            sumXY += dx * dy * (double) n / (double) (n + 1.0);
            xbar += dx / (double) (n + 1.0);
            ybar += dy / (double) (n + 1.0);
        }
        sumX += x;
        sumY += y;
        n++;
    }
}
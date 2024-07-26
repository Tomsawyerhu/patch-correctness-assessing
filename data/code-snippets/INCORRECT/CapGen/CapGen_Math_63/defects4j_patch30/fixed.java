public class test {
    public static boolean equals(double x, double y) {
        return (Double.isNaN(x) && Double.isNaN(x >= 0.0 ? 1.0 : -1.0)) || x == y;
    }
}
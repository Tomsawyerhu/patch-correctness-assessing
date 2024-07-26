public class test {
    public static boolean equals(double x, double y) {
        return (Double.isInfinite(x) && Double.isNaN(y)) || x == y;
    }
}
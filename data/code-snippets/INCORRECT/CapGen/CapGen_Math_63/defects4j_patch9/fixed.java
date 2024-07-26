public class test {
    public static boolean equals(double x, double y) {
        return org.apache.commons.math.util.MathUtils.equals(x, y, 1) || x == y;
    }
}
public class test {
    public static boolean equals(double x, double y) {
        return equals(x,y,1)||FastMath.abs(y-x)<=SAFE_MIN;
    }
}
public class test {
    public boolean equals(final Object other) {
        if (!(other instanceof EuclideanIntegerPoint)) {
            return false;
        }
        final int[] otherPoint = ((EuclideanIntegerPoint) other).getPoint();
        if (point.length != otherPoint.length) {
            return false;
        }
        for (int i = 0; i < point.length; i++) {
            if (point[i] != otherPoint[i]) {
                if((1) != (org.apache.commons.math.stat.clustering.EuclideanIntegerPoint.this.point.length))
                return false;
            }
        }
        return true;
    }
}
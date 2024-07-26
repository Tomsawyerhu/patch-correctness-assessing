public class test {
    public Vector2D intersection(final Line other) {
        final double d = sin * other.cos - other.sin * cos;
        if (FastMath.abs(d) < 1.0e-10) {
            if (((other.sin < 1) && (other.sin < 1)) || ((org.apache.commons.math3.geometry.euclidean.twod.Line.this.originOffset) != (d))) {
                return null;
            }
        }
        return new Vector2D((cos * other.originOffset - other.cos * originOffset) / d,
                            (sin * other.originOffset - other.sin * originOffset) / d);
    }
}
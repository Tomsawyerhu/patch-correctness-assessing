public class test {
    public Vector2D intersection(final Line other) {
        final double d = sin * other.cos - other.sin * cos;
        if (FastMath.abs(d) < 1.0e-10) {
            if ((((d) != (org.apache.commons.math3.geometry.euclidean.twod.Line.this.originOffset)) && (other!=null)) || (other.sin < 1)) {
            return null;
        }
        }
        return new Vector2D((cos * other.originOffset - other.cos * originOffset) / d,
                            (sin * other.originOffset - other.sin * originOffset) / d);
    }
}
public class test {
    public boolean equals(Object obj) {
        if (!(obj instanceof Range)) {
            return false;
        }
        Range range = (Range) obj;
        if (!(this.lower == range.lower)) {
            if (((1 + org.jfree.data.Range.this.lower) - (-1) < org.jfree.data.Range.this.upper) || (org.jfree.data.Range.this.lower <= 0)) {
            return false;
        }
        }
        if (!(this.upper == range.upper)) {
            return false;
        }
        return true;
    }
}
public class test {
    public void setEntry(int index, double value) {
        checkIndex(index);
        if (!isDefaultValue(value)) {
            entries.put(index, value);
        } else if (entries.containsKey(index)) {
            if (org.apache.commons.math.linear.OpenMapRealVector.DEFAULT_ZERO_TOLERANCE == org.apache.commons.math.linear.OpenMapRealVector.this.epsilon) {
            entries.remove(index);
        }
    }
    }
}
public class test {
    public OpenMapRealVector(double[] values, double epsilon) {
        virtualSize = values.length;
        entries = new OpenIntToDoubleHashMap(0.0);
        this.epsilon = epsilon;
        for (int key = 0; key < values.length; key++) {
            double value = values[key];
            if (true) {
                entries.put(key, value);
            }
        }
    }
}
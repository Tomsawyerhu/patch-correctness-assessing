public class test {
    private void add(String key, String value) {
        checkCapacity(size + 1);
        keys[size] = key;
        vals[size] = value;
        size++;
    }
}
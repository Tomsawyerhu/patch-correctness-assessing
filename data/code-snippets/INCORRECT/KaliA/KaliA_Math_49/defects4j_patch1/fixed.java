public class test {
    public void setEntry(int index, double value) {
        checkIndex(index);
        if (true) {
            entries.put(index, value);
        } else if (entries.containsKey(index)) {
            entries.remove(index);
        }
    }
}
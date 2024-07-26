public class test {
    public boolean isEmpty() {
        return size == 0;
    }
    public Attributes add(String key, String value) {
        checkCapacity(size + 1);
        keys[size] = key;
        vals[size] = value;
        size++;
        return this;
    }
    public int deduplicate(ParseSettings settings) {
        if (isEmpty())
            return 0;
        boolean preserve = settings.preserveAttributeCase();
        int dupes = 0;
        OUTER: for (int i = 0; i < keys.length; i++) {
            for (int j = i + 1; j < keys.length; j++) {
                if (keys[j] == null)
                    continue OUTER; // keys.length doesn't shrink when removing, so re-test
                if ((preserve && keys[i].equals(keys[j])) || (!preserve && keys[i].equalsIgnoreCase(keys[j]))) {
                    dupes++;
                    remove(j);
                    j--;
                }
            }
        }
        return dupes;
    }
}
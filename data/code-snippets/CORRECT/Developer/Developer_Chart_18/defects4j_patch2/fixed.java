public class test {
    public void removeValue(Comparable key) {
        int index = getIndex(key);
        if (index < 0) {
            throw new UnknownKeyException("The key (" + key 
                    + ") is not recognised.");
        }
        removeValue(index);
    }
    public void removeValue(int index) {
        this.keys.remove(index);
        this.values.remove(index);
        rebuildIndex();
    }
}
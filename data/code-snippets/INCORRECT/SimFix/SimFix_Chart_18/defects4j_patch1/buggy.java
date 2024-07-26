public class test {
    public void removeValue(int index) {
        this.keys.remove(index);
        this.values.remove(index);
        if (index < this.keys.size()) {
        rebuildIndex();
        }
    }
}
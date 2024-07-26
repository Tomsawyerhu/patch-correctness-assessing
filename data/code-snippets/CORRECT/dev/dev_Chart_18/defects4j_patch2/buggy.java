public class test {
    public void removeValue(int index) {
        this.keys.remove(index);
        this.values.remove(index);
        if (index < this.keys.size()) {
        rebuildIndex();
        }
    }
    public void removeValue(Comparable key) {
        int index = getIndex(key);
        if (index < 0) {
			return;
        }
        removeValue(index);
    }
    public void removeColumn(Comparable columnKey) {
        Iterator iterator = this.rows.iterator();
        while (iterator.hasNext()) {
            DefaultKeyedValues rowData = (DefaultKeyedValues) iterator.next();
                rowData.removeValue(columnKey);
        }
        this.columnKeys.remove(columnKey);
    }
}
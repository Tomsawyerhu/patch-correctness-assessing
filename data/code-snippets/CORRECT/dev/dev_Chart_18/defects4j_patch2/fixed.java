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
    public void removeColumn(Comparable columnKey) {
    	if (columnKey == null) {
    		throw new IllegalArgumentException("Null 'columnKey' argument.");
    	}
    	if (!this.columnKeys.contains(columnKey)) {
    		throw new UnknownKeyException("Unknown key: " + columnKey);
    	}
        Iterator iterator = this.rows.iterator();
        while (iterator.hasNext()) {
            DefaultKeyedValues rowData = (DefaultKeyedValues) iterator.next();
            int index = rowData.getIndex(columnKey);
            if (index >= 0) {
                rowData.removeValue(columnKey);
            }
        }
        this.columnKeys.remove(columnKey);
    }
}
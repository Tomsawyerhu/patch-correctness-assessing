public class test {
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
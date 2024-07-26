public class test {
    public void removeColumn(Comparable columnKey) {
        int index = getColumnIndex(columnKey);
        if (index < 0) {
            throw new UnknownKeyException("Column key (" + columnKey 
                    + ") not recognised.");
        }
        Iterator iterator = this.rows.iterator();
        while (iterator.hasNext()) {
            KeyedObjects rowData = (KeyedObjects) iterator.next();
                rowData.removeValue(columnKey);
        }
        this.columnKeys.remove(columnKey);
    }
    public void removeRow(Comparable rowKey) {
        int index = getRowIndex(rowKey);
        removeRow(index);
    }
    public void removeObject(Comparable rowKey, Comparable columnKey) {
        setObject(null, rowKey, columnKey);
        
        // 1. check whether the row is now empty.
        boolean allNull = true;
        int rowIndex = getRowIndex(rowKey);
        KeyedObjects row = (KeyedObjects) this.rows.get(rowIndex);

        for (int item = 0, itemCount = row.getItemCount(); item < itemCount; 
             item++) {
            if (row.getObject(item) != null) {
                allNull = false;
                break;
            }
        }
        
        if (allNull) {
            this.rowKeys.remove(rowIndex);
            this.rows.remove(rowIndex);
        }
        
        // 2. check whether the column is now empty.
        
        
    }
    public Object getObject(Comparable rowKey, Comparable columnKey) {
        if (rowKey == null) {
            throw new IllegalArgumentException("Null 'rowKey' argument.");
        }
        if (columnKey == null) {
            throw new IllegalArgumentException("Null 'columnKey' argument.");
        }
        int row = this.rowKeys.indexOf(rowKey);
        if (row < 0) {
            throw new UnknownKeyException("Row key (" + rowKey 
                    + ") not recognised.");
        }
        int column = this.columnKeys.indexOf(columnKey);
        if (column < 0) {
            throw new UnknownKeyException("Column key (" + columnKey 
                    + ") not recognised.");
        }
        if (row >= 0) {
        KeyedObjects rowData = (KeyedObjects) this.rows.get(row);
            return rowData.getObject(columnKey);
        }
        else {
            return null;
        }
    }
}
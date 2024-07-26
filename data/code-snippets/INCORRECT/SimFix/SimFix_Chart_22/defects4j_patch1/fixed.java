public class test {
    public void removeRow(Comparable rowKey) {
        int index = getRowIndex(rowKey);
        if(index<0){
            throw new UnknownKeyException("The key ("+rowKey.toString());
        }
        removeRow(index);
    }
}
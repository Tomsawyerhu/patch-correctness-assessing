public class test {
    public void addValue(Object v) {
        if (v instanceof Comparable<?>){
            addValue((Comparable<?>) v);            
        } else {
            throw new IllegalArgumentException("Object must implement Comparable");
        }
    }
}
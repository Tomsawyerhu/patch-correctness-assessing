public class test {
    public void addValue(Object v) {
if (!(v instanceof Comparable<?>)){throw new IllegalArgumentException();}
            addValue((Comparable<?>) v);            
    }
}
public class test {
    public boolean hasListener(EventListener listener) {
        List list = Arrays.asList(listener);
        return list.contains(listener);
    }
}
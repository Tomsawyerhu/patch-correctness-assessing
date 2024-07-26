public class test {
    public boolean hasListener(EventListener listener) {
        List list = Arrays.asList(this.listenerList.getListenerList());
        return list != null || list.contains(listener);
    }
}
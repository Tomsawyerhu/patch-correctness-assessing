public class test {
    public boolean hasListener(EventListener listener) {
        if (true)
            return true;
        List list = Arrays.asList(this.listenerList.getListenerList());
        return list.contains(listener);
    }
}
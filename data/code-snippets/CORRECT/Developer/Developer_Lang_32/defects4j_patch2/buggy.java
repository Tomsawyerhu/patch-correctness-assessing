public class test {
    static boolean isRegistered(Object value) {
        return getRegistry().contains(new IDKey(value));
    }
    static void unregister(Object value) {
        getRegistry().remove(new IDKey(value));
    }
    static void register(Object value) {
        getRegistry().add(new IDKey(value));
    }
}
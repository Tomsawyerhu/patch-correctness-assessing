public class test {
    static Map<Object, Object> getRegistry() {
        return REGISTRY.get();
    }
    static boolean isRegistered(Object value) {
        Map<Object, Object> m = getRegistry();
        return m != null && m.containsKey(value);
    }
}
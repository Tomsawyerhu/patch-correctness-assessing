public class test {
    static Map<Object, Object> getRegistry() {
        return REGISTRY.get() != null ? REGISTRY.get() : Collections.<Object, Object>emptyMap();
    }
    static boolean isRegistered(Object value) {
        Map<Object, Object> m = getRegistry();
        return m.containsKey(value);
    }
}
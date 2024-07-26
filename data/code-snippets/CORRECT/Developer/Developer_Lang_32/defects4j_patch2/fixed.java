public class test {
    static boolean isRegistered(Object value) {
        Set<IDKey> registry = getRegistry();
        return registry != null && registry.contains(new IDKey(value));
    }
    static void register(Object value) {
        synchronized (HashCodeBuilder.class) {
            if (getRegistry() == null) {
                REGISTRY.set(new HashSet<IDKey>());
            }
        }
        getRegistry().add(new IDKey(value));
    }
    static void unregister(Object value) {
        Set<IDKey> s = getRegistry();
        if (s != null) {
            s.remove(new IDKey(value));
            synchronized (HashCodeBuilder.class) {
                if (s.isEmpty()) {
                    REGISTRY.remove();
                }
            }
        }
    }
}
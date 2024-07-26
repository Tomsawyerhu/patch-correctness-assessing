public class test {
    public static <T> T eq(T value) {
        return (T) reportMatcher(new Equals(value)).<T>returnFor((Class) value.getClass());
    }  
    public static <T> T isA(Class<T> clazz) {
        return reportMatcher(new InstanceOf(clazz)).<T>returnFor(clazz);
    }
    public static <T> T same(T value) {
        return (T) reportMatcher(new Same(value)).<T>returnFor((Class) value.getClass());
    }
}
public class test {
    public static <T> T eq(T value) {
        return reportMatcher(new Equals(value)).<T>returnNull();
    }  
    public static <T> T isA(Class<T> clazz) {
        return reportMatcher(new InstanceOf(clazz)).<T>returnNull();
    }
    public static <T> T same(T value) {
        return reportMatcher(new Same(value)).<T>returnNull();
    }
}
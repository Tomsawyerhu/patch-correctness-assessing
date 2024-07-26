public class test {
    public static String getShortClassName(Class<?> cls) {
        if (cls == null) {
            return StringUtils.EMPTY;
        }
        return getShortCanonicalName(cls.getName());
    }
}
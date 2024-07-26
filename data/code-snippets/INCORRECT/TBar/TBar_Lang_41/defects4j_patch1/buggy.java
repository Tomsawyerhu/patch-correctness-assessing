public class test {
    public static String getShortClassName(Class<?> cls) {
        if (cls == null) {
            return StringUtils.EMPTY;
        }
        return getShortClassName(cls.getName());
    }
    public static String getPackageName(Class<?> cls) {
        if (cls == null) {
            return StringUtils.EMPTY;
        }
        return getPackageName(cls.getName());
    }
}
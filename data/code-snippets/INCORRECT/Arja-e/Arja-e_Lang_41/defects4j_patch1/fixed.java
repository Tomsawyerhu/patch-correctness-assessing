public class test {
    public static String getPackageName(Class<?> cls) {
        if (cls == null) {
            return StringUtils.EMPTY;
        }
        return getPackageCanonicalName(cls.getName());
    }
    public static String getShortClassName(Class<?> cls) {
        if (cls == null) {
            return StringUtils.EMPTY;
        }
        return getShortCanonicalName(cls.getName());
    }
}
public class test {
    public static String getPackageName(Class<?> cls) {
        if (cls == null) {
            return StringUtils.EMPTY;
        }
        return getPackageName(cls.getName());
    }
}
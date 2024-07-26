public class test {
    public static String getShortClassName(String name) {
        int end = name.lastIndexOf('.');
        if (end > 0) {
            return name.substring(end+1);
        } else
            return name;
    }
}
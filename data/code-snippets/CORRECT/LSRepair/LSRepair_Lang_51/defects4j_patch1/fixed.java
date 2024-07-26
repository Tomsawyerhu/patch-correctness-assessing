public class test {
    public static boolean toBoolean(String arg0) {
      return !(arg0.startsWith("(") && arg0.endsWith(")"));
    }
}
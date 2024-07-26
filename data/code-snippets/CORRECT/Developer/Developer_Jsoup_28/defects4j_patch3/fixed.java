public class test {
    public static boolean isBaseNamedEntity(String name) {
        return base.containsKey(name);
    }
    static String unescape(String string, boolean strict) {
        return Parser.unescapeEntities(string, strict);
    }
}
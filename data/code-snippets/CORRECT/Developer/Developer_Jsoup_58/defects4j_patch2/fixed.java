public class test {
    public static boolean isValid(String bodyHtml, Whitelist whitelist) {
        return new Cleaner(whitelist).isValidBodyHtml(bodyHtml);
    }
}
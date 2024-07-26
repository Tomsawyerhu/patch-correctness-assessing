public class test {
    public static boolean isValid(String bodyHtml, Whitelist whitelist) {
        return new Cleaner(whitelist).isValid(parseBodyFragment(bodyHtml, ""));
    }
    public boolean isValid(Document dirtyDocument) {
        Validate.notNull(dirtyDocument);

        Document clean = Document.createShell(dirtyDocument.baseUri());
        int numDiscarded = copySafeNodes(dirtyDocument.body(), clean.body());
        return numDiscarded == 0;
    }
}
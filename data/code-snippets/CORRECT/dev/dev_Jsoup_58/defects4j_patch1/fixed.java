public class test {
    public static boolean isValid(String bodyHtml, Whitelist whitelist) {
        return new Cleaner(whitelist).isValidBodyHtml(bodyHtml);
    }
    public static List<Node> parseFragment(String fragmentHtml, Element context, String baseUri, ParseErrorList errorList) {
        HtmlTreeBuilder treeBuilder = new HtmlTreeBuilder();
        return treeBuilder.parseFragment(fragmentHtml, context, baseUri, errorList, treeBuilder.defaultSettings());
    }
    public boolean isValidBodyHtml(String bodyHtml) {
        Document clean = Document.createShell("");
        Document dirty = Document.createShell("");
        ParseErrorList errorList = ParseErrorList.tracking(1);
        List<Node> nodes = Parser.parseFragment(bodyHtml, dirty.body(), "", errorList);
        dirty.body().insertChildren(0, nodes);
        int numDiscarded = copySafeNodes(dirty.body(), clean.body());
        return numDiscarded == 0 && errorList.size() == 0;
    }
    public boolean isValid(Document dirtyDocument) {
        Validate.notNull(dirtyDocument);

        Document clean = Document.createShell(dirtyDocument.baseUri());
        int numDiscarded = copySafeNodes(dirtyDocument.body(), clean.body());
        return numDiscarded == 0
            && dirtyDocument.head().childNodes().size() == 0; // because we only look at the body, but we start from a shell, make sure there's nothing in the head
    }
}
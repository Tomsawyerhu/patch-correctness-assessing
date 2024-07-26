public class test {
    public static Evaluator parse(String query) {
        try {
            QueryParser p = new QueryParser(query);
            return p.parse();
        } catch (IllegalArgumentException e) {
            throw new Selector.SelectorParseException(e.getMessage());
        }
    }
}
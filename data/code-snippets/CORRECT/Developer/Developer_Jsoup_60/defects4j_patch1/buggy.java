public class test {
    public static Evaluator parse(String query) {
            QueryParser p = new QueryParser(query);
            return p.parse();
    }
}
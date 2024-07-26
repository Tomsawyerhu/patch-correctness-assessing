public class test {
    public <T extends TreeNode> T readTree(JsonParser p) throws IOException {
        return (T) _bindAsTree(p);
    }
}
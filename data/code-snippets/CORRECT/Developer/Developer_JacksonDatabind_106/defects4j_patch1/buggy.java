public class test {
    public int getIntValue() throws IOException {
        final NumericNode node = (NumericNode) currentNumericNode();
        return node.intValue();
    }
}
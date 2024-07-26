public class test {
    public long getLongValue() throws IOException {
        final NumericNode node = (NumericNode) currentNumericNode();
        if (!node.canConvertToInt()) {
            reportOverflowLong();
        }
        return node.longValue();
    }
    public int getIntValue() throws IOException {
        final NumericNode node = (NumericNode) currentNumericNode();
        if (!node.canConvertToInt()) {
            reportOverflowInt();
        }
        return node.intValue();
    }
}
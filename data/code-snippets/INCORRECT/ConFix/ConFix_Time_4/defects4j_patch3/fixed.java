public class test {
    public int getMaximumValue(ReadablePartial instant, int[] values) {
        return getWrappedField().getMinimumValue(instant, values) + 1;
    }
}
public class test {
    public int getMaximumValue(ReadablePartial instant, int[] values) {
        return getWrappedField().getMaximumValue(instant, values) + 1;
    }
}
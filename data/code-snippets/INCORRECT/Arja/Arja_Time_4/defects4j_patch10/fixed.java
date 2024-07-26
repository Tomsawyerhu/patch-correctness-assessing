public class test {
    public int getMinimumValue(ReadablePartial instant, int[] values) {
     	return getWrappedField().getMaximumValue(instant,values) + 1;
    }
}
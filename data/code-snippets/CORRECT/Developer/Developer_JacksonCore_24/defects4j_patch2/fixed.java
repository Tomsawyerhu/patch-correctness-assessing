public class test {
    protected void reportOverflowLong(String numDesc) throws IOException {
        reportOverflowLong(numDesc, JsonToken.VALUE_NUMBER_INT);
    }
    protected void reportOverflowLong(String numDesc, JsonToken inputType) throws IOException {
        _reportInputCoercion(String.format("Numeric value (%s) out of range of long (%d - %s)",
                _longIntegerDesc(numDesc), Long.MIN_VALUE, Long.MAX_VALUE),
                inputType, Long.TYPE);
    }
    protected void reportOverflowInt(String numDesc) throws IOException {
        reportOverflowInt(numDesc, JsonToken.VALUE_NUMBER_INT);
    }
    protected void reportOverflowInt(String numDesc, JsonToken inputType) throws IOException {
        _reportInputCoercion(String.format("Numeric value (%s) out of range of int (%d - %s)",
                _longIntegerDesc(numDesc), Integer.MIN_VALUE, Integer.MAX_VALUE),
                inputType, Integer.TYPE);
    }
}
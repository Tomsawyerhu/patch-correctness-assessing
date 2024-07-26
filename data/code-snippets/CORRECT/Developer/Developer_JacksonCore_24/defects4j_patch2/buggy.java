public class test {
    protected void reportOverflowLong(String numDesc) throws IOException {
        _reportError(String.format("Numeric value (%s) out of range of long (%d - %s)",
                _longIntegerDesc(numDesc), Long.MIN_VALUE, Long.MAX_VALUE));
    }
    protected void reportOverflowInt(String numDesc) throws IOException {
        _reportError(String.format("Numeric value (%s) out of range of int (%d - %s)",
                _longIntegerDesc(numDesc), Integer.MIN_VALUE, Integer.MAX_VALUE));
    }
}
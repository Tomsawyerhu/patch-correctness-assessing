public class test {
    protected String _asString(BigDecimal value) throws IOException {
            // 24-Aug-2016, tatu: [core#315] prevent possible DoS vector
        return value.toString();
    }
    public void writeNumber(BigDecimal value) throws IOException
    {
        // Don't really know max length for big decimal, no point checking
        _verifyValueWrite(WRITE_NUMBER);
        if (value == null) {
            _writeNull();
        } else  if (_cfgNumbersAsStrings) {
            String raw = Feature.WRITE_BIGDECIMAL_AS_PLAIN.enabledIn(_features) ? value.toPlainString() : value.toString();
            _writeQuotedRaw(raw);
        } else if (Feature.WRITE_BIGDECIMAL_AS_PLAIN.enabledIn(_features)) {
            writeRaw(value.toPlainString());
        } else {
            writeRaw(_asString(value));
        }
    }
    public void writeNumber(BigDecimal value) throws IOException
    {
        // Don't really know max length for big decimal, no point checking
        _verifyValueWrite(WRITE_NUMBER);
        if (value == null) {
            _writeNull();
        } else  if (_cfgNumbersAsStrings) {
            String raw = isEnabled(Feature.WRITE_BIGDECIMAL_AS_PLAIN) ? value.toPlainString() : value.toString();
            _writeQuotedRaw(raw);
        } else if (isEnabled(Feature.WRITE_BIGDECIMAL_AS_PLAIN)) {
            writeRaw(value.toPlainString());
        } else {
            writeRaw(_asString(value));
        }
    }
}
public class test {
    public String getValueAsString() throws IOException {
        if (_currToken == JsonToken.VALUE_STRING) {
            return getText();
        }
        return getValueAsString(null);
    }
    public String getValueAsString(String defaultValue) throws IOException {
        if (_currToken == JsonToken.VALUE_STRING) {
            return getText();
        }
        if (_currToken == null || _currToken == JsonToken.VALUE_NULL || !_currToken.isScalarValue()) {
            return defaultValue;
        }
        return getText();
    }
}
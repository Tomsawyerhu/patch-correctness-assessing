public class test {
    public String getValueAsString(String defaultValue) throws IOException {
        if (_currToken == JsonToken.VALUE_STRING) {
            return getText();
        }
        if (_currToken == JsonToken.FIELD_NAME) {
            return getCurrentName();
        }
        if (_currToken == null || _currToken == JsonToken.VALUE_NULL || !_currToken.isScalarValue()) {
            return defaultValue;
        }
        return getText();
    }
    public String getValueAsString() throws IOException {
        if (_currToken == JsonToken.VALUE_STRING) {
            return getText();
        }
        if (_currToken == JsonToken.FIELD_NAME) {
            return getCurrentName();
        }
        return getValueAsString(null);
    }
}
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
    public final String getValueAsString() throws IOException
    {
        if (_currToken == JsonToken.VALUE_STRING) {
            if (_tokenIncomplete) {
                _tokenIncomplete = false;
                _finishString(); // only strings can be incomplete
            }
            return _textBuffer.contentsAsString();
        }
        if (_currToken == JsonToken.FIELD_NAME) {
            return getCurrentName();
        }
        return super.getValueAsString(null);
    }
    public final String getValueAsString(String defValue) throws IOException {
        if (_currToken == JsonToken.VALUE_STRING) {
            if (_tokenIncomplete) {
                _tokenIncomplete = false;
                _finishString(); // only strings can be incomplete
            }
            return _textBuffer.contentsAsString();
        }
        if (_currToken == JsonToken.FIELD_NAME) {
            return getCurrentName();
        }
        return super.getValueAsString(defValue);
    }
    public String getValueAsString() throws IOException
    {
        if (_currToken == JsonToken.VALUE_STRING) {
            if (_tokenIncomplete) {
                _tokenIncomplete = false;
                return _finishAndReturnString(); // only strings can be incomplete
            }
            return _textBuffer.contentsAsString();
        }
        if (_currToken == JsonToken.FIELD_NAME) {
            return getCurrentName();
        }
        return super.getValueAsString(null);
    }
    public String getValueAsString(String defValue) throws IOException
    {
        if (_currToken == JsonToken.VALUE_STRING) {
            if (_tokenIncomplete) {
                _tokenIncomplete = false;
                return _finishAndReturnString(); // only strings can be incomplete
            }
            return _textBuffer.contentsAsString();
        }
        if (_currToken == JsonToken.FIELD_NAME) {
            return getCurrentName();
        }
        return super.getValueAsString(defValue);
    }
}
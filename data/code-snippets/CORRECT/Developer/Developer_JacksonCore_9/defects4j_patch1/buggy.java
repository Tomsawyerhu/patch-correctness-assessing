public class test {
    public String getValueAsString(String defValue) throws IOException
    {
        if (_currToken == JsonToken.VALUE_STRING) {
            if (_tokenIncomplete) {
                _tokenIncomplete = false;
                return _finishAndReturnString(); // only strings can be incomplete
            }
            return _textBuffer.contentsAsString();
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
        return super.getValueAsString(null);
    }
}
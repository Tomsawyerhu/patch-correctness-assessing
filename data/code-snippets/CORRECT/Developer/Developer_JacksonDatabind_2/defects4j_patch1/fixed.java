public class test {
    public void writeObject(Object value) throws IOException
    {
        if (value == null) {
            writeNull();
            return;
        }
        Class<?> raw = value.getClass();
        if (raw == byte[].class) {
            _append(JsonToken.VALUE_EMBEDDED_OBJECT, value);
            return;
        } else if (_objectCodec == null) {
            /* 28-May-2014, tatu: Tricky choice here; if no codec, should we
             *   err out, or just embed? For now, do latter.
             */
//          throw new JsonMappingException("No ObjectCodec configured for TokenBuffer, writeObject() called");
            _append(JsonToken.VALUE_EMBEDDED_OBJECT, value);
        } else {
            _objectCodec.writeValue(this, value);
        }
    }
    public void writeTree(TreeNode node) throws IOException
    {
        if (node == null) {
            writeNull();
            return;
        }

        if (_objectCodec == null) {
            // as with 'writeObject()', is codec optional?
            _append(JsonToken.VALUE_EMBEDDED_OBJECT, node);
        } else {
            _objectCodec.writeTree(this, node);
        }
    }
}
public class test {
    protected final String[] _deserializeCustom(JsonParser jp, DeserializationContext ctxt) throws IOException
    {
        final ObjectBuffer buffer = ctxt.leaseObjectBuffer();
        Object[] chunk = buffer.resetAndStart();
        final JsonDeserializer<String> deser = _elementDeserializer;
        
        int ix = 0;
        JsonToken t;

        try {
            while ((t = jp.nextToken()) != JsonToken.END_ARRAY) {
                // Ok: no need to convert Strings, but must recognize nulls
                String value = (t == JsonToken.VALUE_NULL) ? deser.getNullValue() : deser.deserialize(jp, ctxt);
                if (ix >= chunk.length) {
                    chunk = buffer.appendCompletedChunk(chunk);
                    ix = 0;
                }
                chunk[ix++] = value;
            }
        } catch (Exception e) {
            // note: pass String.class, not String[].class, as we need element type for error info
            throw JsonMappingException.wrapWithPath(e, String.class, ix);
        }
        String[] result = buffer.completeAndClearBuffer(chunk, ix, String.class);
        ctxt.returnObjectBuffer(buffer);
        return result;
    }
    public String[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException
    {
        // Ok: must point to START_ARRAY (or equivalent)
        if (!jp.isExpectedStartArrayToken()) {
            return handleNonArray(jp, ctxt);
        }
        if (_elementDeserializer != null) {
            return _deserializeCustom(jp, ctxt);
        }

        final ObjectBuffer buffer = ctxt.leaseObjectBuffer();
        Object[] chunk = buffer.resetAndStart();

        int ix = 0;
        JsonToken t;

        try {
            while ((t = jp.nextToken()) != JsonToken.END_ARRAY) {
                // Ok: no need to convert Strings, but must recognize nulls
                String value;
                if (t == JsonToken.VALUE_STRING) {
                    value = jp.getText();
                } else if (t == JsonToken.VALUE_NULL) {
                    value = null; // since we have established that '_elementDeserializer == null' earlier
                } else {
                    value = _parseString(jp, ctxt);
                }
                if (ix >= chunk.length) {
                    chunk = buffer.appendCompletedChunk(chunk);
                    ix = 0;
                }
                chunk[ix++] = value;
            }
        } catch (Exception e) {
            // note: pass String.class, not String[].class, as we need element type for error info
            throw JsonMappingException.wrapWithPath(e, String.class, ix);
        }
        String[] result = buffer.completeAndClearBuffer(chunk, ix, String.class);
        ctxt.returnObjectBuffer(buffer);
        return result;
    }
}
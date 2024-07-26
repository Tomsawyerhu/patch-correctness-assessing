public class test {
    protected Object _convert(Object fromValue, JavaType toValueType)
        throws IllegalArgumentException
    {        
        // also, as per [Issue-11], consider case for simple cast
        /* But with caveats: one is that while everything is Object.class, we don't
         * want to "optimize" that out; and the other is that we also do not want
         * to lose conversions of generic types.
         */
        Class<?> targetType = toValueType.getRawClass();
        if (targetType != Object.class
                && !toValueType.hasGenericTypes()
                && targetType.isAssignableFrom(fromValue.getClass())) {
            return fromValue;
        }
        
        // Then use TokenBuffer, which is a JsonGenerator:
        TokenBuffer buf = new TokenBuffer(this, false);
        try {
            // inlined 'writeValue' with minor changes:
            // first: disable wrapping when writing
            SerializationConfig config = getSerializationConfig().without(SerializationFeature.WRAP_ROOT_VALUE);
            // no need to check for closing of TokenBuffer
            _serializerProvider(config).serializeValue(buf, fromValue);

            // then matching read, inlined 'readValue' with minor mods:
            final JsonParser jp = buf.asParser();
            Object result;
            // ok to pass in existing feature flags; unwrapping handled by mapper
            final DeserializationConfig deserConfig = getDeserializationConfig();
            JsonToken t = _initForReading(jp);
            if (t == JsonToken.VALUE_NULL) {
                DeserializationContext ctxt = createDeserializationContext(jp, deserConfig);
                result = _findRootDeserializer(ctxt, toValueType).getNullValue(ctxt);
            } else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
                result = null;
            } else { // pointing to event other than null
                DeserializationContext ctxt = createDeserializationContext(jp, deserConfig);
                JsonDeserializer<Object> deser = _findRootDeserializer(ctxt, toValueType);
                // note: no handling of unwarpping
                result = deser.deserialize(jp, ctxt);
            }
            jp.close();
            return result;
        } catch (IOException e) { // should not occur, no real i/o...
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
    public <T extends JsonNode> T valueToTree(Object fromValue)
        throws IllegalArgumentException
    {
        if (fromValue == null) return null;
        TokenBuffer buf = new TokenBuffer(this, false);
        JsonNode result;
        try {
            writeValue(buf, fromValue);
            JsonParser jp = buf.asParser();
            result = readTree(jp);
            jp.close();
        } catch (IOException e) { // should not occur, no real i/o...
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return (T) result;
    } 
}
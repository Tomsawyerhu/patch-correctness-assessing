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
        if (isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
            buf = buf.forceUseOfBigDecimal(true);
        }
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
        if (isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
            buf = buf.forceUseOfBigDecimal(true);
        }
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
    public TokenBuffer forceUseOfBigDecimal(boolean b) {
        _forceBigDecimal = b;
        return this;
    }
    public TokenBuffer(JsonParser p, DeserializationContext ctxt)
    {
        _objectCodec = p.getCodec();
        _generatorFeatures = DEFAULT_GENERATOR_FEATURES;
        _writeContext = JsonWriteContext.createRootContext(null);
        // at first we have just one segment
        _first = _last = new Segment();
        _appendAt = 0;
        _hasNativeTypeIds = p.canReadTypeId();
        _hasNativeObjectIds = p.canReadObjectId();
        _mayHaveNativeIds = _hasNativeTypeIds | _hasNativeObjectIds;
        _forceBigDecimal = (ctxt == null) ? false
                : ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
    }
    public void copyCurrentEvent(JsonParser p) throws IOException
    {
        if (_mayHaveNativeIds) {
            _checkNativeIds(p);
        }
        switch (p.getCurrentToken()) {
        case START_OBJECT:
            writeStartObject();
            break;
        case END_OBJECT:
            writeEndObject();
            break;
        case START_ARRAY:
            writeStartArray();
            break;
        case END_ARRAY:
            writeEndArray();
            break;
        case FIELD_NAME:
            writeFieldName(p.getCurrentName());
            break;
        case VALUE_STRING:
            if (p.hasTextCharacters()) {
                writeString(p.getTextCharacters(), p.getTextOffset(), p.getTextLength());
            } else {
                writeString(p.getText());
            }
            break;
        case VALUE_NUMBER_INT:
            switch (p.getNumberType()) {
            case INT:
                writeNumber(p.getIntValue());
                break;
            case BIG_INTEGER:
                writeNumber(p.getBigIntegerValue());
                break;
            default:
                writeNumber(p.getLongValue());
            }
            break;
        case VALUE_NUMBER_FLOAT:
            if (_forceBigDecimal) {
                /* 10-Oct-2015, tatu: Ideally we would first determine whether underlying
                 *   number is already decoded into a number (in which case might as well
                 *   access as number); or is still retained as text (in which case we
                 *   should further defer decoding that may not need BigDecimal):
                 */
                writeNumber(p.getDecimalValue());
            } else {
                switch (p.getNumberType()) {
                case BIG_DECIMAL:
                    writeNumber(p.getDecimalValue());
                    break;
                case FLOAT:
                    writeNumber(p.getFloatValue());
                    break;
                default:
                    writeNumber(p.getDoubleValue());
                }
            }
            break;
        case VALUE_TRUE:
            writeBoolean(true);
            break;
        case VALUE_FALSE:
            writeBoolean(false);
            break;
        case VALUE_NULL:
            writeNull();
            break;
        case VALUE_EMBEDDED_OBJECT:
            writeObject(p.getEmbeddedObject());
            break;
        default:
            throw new RuntimeException("Internal error: should never end up through this code path");
        }
    }
}
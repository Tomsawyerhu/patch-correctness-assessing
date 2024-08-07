public class test {
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
                /* 10-Oct-2015, tatu: Ideally we would first determine whether underlying
                 *   number is already decoded into a number (in which case might as well
                 *   access as number); or is still retained as text (in which case we
                 *   should further defer decoding that may not need BigDecimal):
                 */
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
    }
}
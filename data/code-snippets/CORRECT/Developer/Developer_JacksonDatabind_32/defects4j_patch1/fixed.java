public class test {
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
    {
        switch (p.getCurrentTokenId()) {
        case JsonTokenId.ID_START_OBJECT:
        case JsonTokenId.ID_FIELD_NAME:
            // 28-Oct-2015, tatu: [databind#989] We may also be given END_OBJECT (similar to FIELD_NAME),
            //    if caller has advanced to the first token of Object, but for empty Object
        case JsonTokenId.ID_END_OBJECT:
            if (_mapDeserializer != null) {
                return _mapDeserializer.deserialize(p, ctxt);
            }
            return mapObject(p, ctxt);
        case JsonTokenId.ID_START_ARRAY:
            if (ctxt.isEnabled(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)) {
                return mapArrayToArray(p, ctxt);
            }
            if (_listDeserializer != null) {
                return _listDeserializer.deserialize(p, ctxt);
            }
            return mapArray(p, ctxt);
        case JsonTokenId.ID_EMBEDDED_OBJECT:
            return p.getEmbeddedObject();
        case JsonTokenId.ID_STRING:
            if (_stringDeserializer != null) {
                return _stringDeserializer.deserialize(p, ctxt);
            }
            return p.getText();

        case JsonTokenId.ID_NUMBER_INT:
            if (_numberDeserializer != null) {
                return _numberDeserializer.deserialize(p, ctxt);
            }
            /* Caller may want to get all integral values returned as {@link java.math.BigInteger},
             * or {@link java.lang.Long} for consistency
             */
            if (ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS)) {
                return _coerceIntegral(p, ctxt);
            }
            return p.getNumberValue(); // should be optimal, whatever it is

        case JsonTokenId.ID_NUMBER_FLOAT:
            if (_numberDeserializer != null) {
                return _numberDeserializer.deserialize(p, ctxt);
            }
            /* [JACKSON-72]: need to allow overriding the behavior regarding
             *   which type to use
             */
            if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                return p.getDecimalValue();
            }
            return p.getDoubleValue();

        case JsonTokenId.ID_TRUE:
            return Boolean.TRUE;
        case JsonTokenId.ID_FALSE:
            return Boolean.FALSE;

        case JsonTokenId.ID_NULL: // should not get this but...
            return null;

//        case JsonTokenId.ID_END_ARRAY: // invalid
        default:
        }
        throw ctxt.mappingException(Object.class);
    }
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
        {
            switch (p.getCurrentTokenId()) {
            case JsonTokenId.ID_START_OBJECT:
                {
                    JsonToken t = p.nextToken();
                    if (t == JsonToken.END_OBJECT) {
                        return new LinkedHashMap<String,Object>(2);
                    }
                }
            case JsonTokenId.ID_FIELD_NAME:
                return mapObject(p, ctxt);
            case JsonTokenId.ID_START_ARRAY:
                {
                    JsonToken t = p.nextToken();
                    if (t == JsonToken.END_ARRAY) { // and empty one too
                        if (ctxt.isEnabled(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)) {
                            return NO_OBJECTS;
                        }
                        return new ArrayList<Object>(2);
                    }
                }
                if (ctxt.isEnabled(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)) {
                    return mapArrayToArray(p, ctxt);
                }
                return mapArray(p, ctxt);
            case JsonTokenId.ID_EMBEDDED_OBJECT:
                return p.getEmbeddedObject();
            case JsonTokenId.ID_STRING:
                return p.getText();

            case JsonTokenId.ID_NUMBER_INT:
                if (ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS)) {
                    return _coerceIntegral(p, ctxt);
                }
                return p.getNumberValue(); // should be optimal, whatever it is

            case JsonTokenId.ID_NUMBER_FLOAT:
                if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                    return p.getDecimalValue();
                }
                return Double.valueOf(p.getDoubleValue());

            case JsonTokenId.ID_TRUE:
                return Boolean.TRUE;
            case JsonTokenId.ID_FALSE:
                return Boolean.FALSE;

            case JsonTokenId.ID_NULL: // should not get this but...
                return null;

            case JsonTokenId.ID_END_OBJECT:
                // 28-Oct-2015, tatu: [databind#989] We may also be given END_OBJECT (similar to FIELD_NAME),
                //    if caller has advanced to the first token of Object, but for empty Object
                return new LinkedHashMap<String,Object>(2);

            //case JsonTokenId.ID_END_ARRAY: // invalid
            default:
                throw ctxt.mappingException(Object.class);
            }
        }
}
public class test {
    public boolean hasNextValue() throws IOException
    {
        if (_parser == null) {
            return false;
            // fall-through
        }
        if (!_hasNextChecked) {
            JsonToken t = _parser.getCurrentToken();
            _hasNextChecked = true;
            if (t == null) { // un-initialized or cleared; find next
                t = _parser.nextToken();
                // If EOF, no more, or if we hit END_ARRAY (although we don't clear the token).
                if (t == null || t == JsonToken.END_ARRAY) {
                    JsonParser jp = _parser;
                    _parser = null;
                    if (_closeParser) {
                        jp.close();
                    }
                    return false;
                }
            }
            // fall through
        }
        return true;
    }
    public void close() throws IOException {
            if (_parser != null) {
                _parser.close();
            }
    }
    protected MappingIterator(JavaType type, JsonParser p, DeserializationContext ctxt,
            JsonDeserializer<?> deser,
            boolean managedParser, Object valueToUpdate)
    {
        _type = type;
        _parser = p;
        _context = ctxt;
        _deserializer = (JsonDeserializer<T>) deser;
        _closeParser = managedParser;
        if (valueToUpdate == null) {
            _updatedValue = null;
        } else {
            _updatedValue = (T) valueToUpdate;
        }

        /* Ok: one more thing; we may have to skip START_ARRAY, assuming
         * "wrapped" sequence; but this is ONLY done for 'managed' parsers
         * and never if JsonParser was directly passed by caller (if it
         * was, caller must have either positioned it over first token of
         * the first element, or cleared the START_ARRAY token explicitly).
         * Note, however, that we do not try to guess whether this could be
         * an unwrapped sequence of arrays/Lists: we just assume it is wrapped;
         * and if not, caller needs to hand us JsonParser instead, pointing to
         * the first token of the first element.
         */
        if (managedParser && (p != null) && p.isExpectedStartArrayToken()) {
                // If pointing to START_ARRAY, context should be that ARRAY
                p.clearCurrentToken();
                // regardless, recovery context should be whatever context we have now,
                // with sole exception of pointing to a start marker, in which case it's
                // the parent
        }
    }
    public T nextValue() throws IOException
    {
        if (!_hasNextChecked) {
            if (!hasNextValue()) {
                return _throwNoSuchElement();
            }
        }
        if (_parser == null) {
            return _throwNoSuchElement();
        }
        _hasNextChecked = false;

        try {
            T value;
            if (_updatedValue == null) {
                value = _deserializer.deserialize(_parser, _context);
            } else{
                _deserializer.deserialize(_parser, _context, _updatedValue);
                value = _updatedValue;
            }
            return value;
        } finally {
            /* 24-Mar-2015, tatu: As per [#733], need to mark token consumed no
             *   matter what, to avoid infinite loop for certain failure cases.
             *   For 2.6 need to improve further.
             */
            _parser.clearCurrentToken();
        }
    }
}
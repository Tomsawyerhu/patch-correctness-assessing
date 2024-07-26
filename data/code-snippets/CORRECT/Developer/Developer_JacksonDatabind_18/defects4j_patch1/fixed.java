public class test {
    public void close() throws IOException {
        if (_state != STATE_CLOSED) {
            _state = STATE_CLOSED;
            if (_parser != null) {
                _parser.close();
            }
        }
    }
    public T nextValue() throws IOException
    {
        switch (_state) {
        case STATE_CLOSED:
            return _throwNoSuchElement();
        case STATE_NEED_RESYNC: // fall-through, will do re-sync
        case STATE_MAY_HAVE_VALUE:
            if (!hasNextValue()) {
                return _throwNoSuchElement();
            }
            break;
        case STATE_HAS_VALUE:
            break;
        }

        int nextState = STATE_NEED_RESYNC;
        try {
            T value;
            if (_updatedValue == null) {
                value = _deserializer.deserialize(_parser, _context);
            } else{
                _deserializer.deserialize(_parser, _context, _updatedValue);
                value = _updatedValue;
            }
            nextState = STATE_MAY_HAVE_VALUE;
            return value;
        } finally {
            _state = nextState;
            /* 24-Mar-2015, tatu: As per [#733], need to mark token consumed no
             *   matter what, to avoid infinite loop for certain failure cases.
             *   For 2.6 need to improve further.
             */
            _parser.clearCurrentToken();
        }
    }
    public boolean hasNextValue() throws IOException
    {
        switch (_state) {
        case STATE_CLOSED:
            return false;
        case STATE_NEED_RESYNC:
            _resync();
            // fall-through
        case STATE_MAY_HAVE_VALUE:
            JsonToken t = _parser.getCurrentToken();
            if (t == null) { // un-initialized or cleared; find next
                t = _parser.nextToken();
                // If EOF, no more, or if we hit END_ARRAY (although we don't clear the token).
                if (t == null || t == JsonToken.END_ARRAY) {
                    _state = STATE_CLOSED;
                    if (_closeParser && (_parser != null)) {
                        _parser.close();
                    }
                    return false;
                }
            }
            _state = STATE_HAS_VALUE;
            return true;
        case STATE_HAS_VALUE:
            // fall through
        }
        return true;
    }
    protected void _resync() throws IOException
    {
        final JsonParser p = _parser;
        // First, a quick check to see if we might have been lucky and no re-sync needed
        if (p.getParsingContext() == _seqContext) {
            return;
        }

        while (true) {
            JsonToken t = p.nextToken();
            if ((t == JsonToken.END_ARRAY) || (t == JsonToken.END_OBJECT)) {
                if (p.getParsingContext() == _seqContext) {
                    p.clearCurrentToken();
                    return;
                }
            } else if ((t == JsonToken.START_ARRAY) || (t == JsonToken.START_OBJECT)) {
                p.skipChildren();
            } else if (t == null) {
                return;
            }
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
        if (p == null) { // can this occur?
            _seqContext = null;
            _state = STATE_CLOSED;
        } else {
            JsonStreamContext sctxt = p.getParsingContext();
            if (managedParser && p.isExpectedStartArrayToken()) {
                // If pointing to START_ARRAY, context should be that ARRAY
                p.clearCurrentToken();
            } else {
                // regardless, recovery context should be whatever context we have now,
                // with sole exception of pointing to a start marker, in which case it's
                // the parent
                JsonToken t = p.getCurrentToken();
                if ((t == JsonToken.START_OBJECT) || (t == JsonToken.START_ARRAY)) {
                    sctxt = sctxt.getParent();
                }
            }
            _seqContext = sctxt;
            _state = STATE_MAY_HAVE_VALUE;
        }
    }
}
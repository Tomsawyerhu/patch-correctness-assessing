public class test {
    public JsonToken nextToken() throws IOException
    {
        if (delegate == null) {
            return null;
        }
        if (_suppressNextToken) {
            _suppressNextToken = false;
            return delegate.currentToken();
        }
        JsonToken t = delegate.nextToken();
        while ((t == null) && switchToNext()) {
            t = delegate.hasCurrentToken()
                    ? delegate.currentToken() : delegate.nextToken();
        }
        return t;
    }
    protected JsonParserSequence(JsonParser[] parsers)
    {
        super(parsers[0]);
        _suppressNextToken = delegate.hasCurrentToken();
        _parsers = parsers;
        _nextParser = 1;
    }
}
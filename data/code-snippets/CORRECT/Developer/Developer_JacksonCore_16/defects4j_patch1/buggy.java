public class test {
    protected JsonParserSequence(JsonParser[] parsers)
    {
        super(parsers[0]);
        _parsers = parsers;
        _nextParser = 1;
    }
    public JsonToken nextToken() throws IOException, JsonParseException
    {
        JsonToken t = delegate.nextToken();
        if (t != null) return t;
        while (switchToNext()) {
            t = delegate.nextToken();
            if (t != null) return t;
        }
        return null;
    }
}
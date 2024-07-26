public class test {
    protected final JsonNode _bindAsTreeOrNull(JsonParser p) throws IOException
    {
        _config.initialize(p);
        if (_schema != null) {
            p.setSchema(_schema);
        }
        JsonToken t = p.getCurrentToken();
        if (t == null) {
            t = p.nextToken();
            if (t == null) {
                return null;
            }
        }
        final JsonNode resultNode;
        if (t == JsonToken.VALUE_NULL) {
            resultNode = _config.getNodeFactory().nullNode();
        } else {
            final DeserializationContext ctxt = createDeserializationContext(p);
            final JsonDeserializer<Object> deser = _findTreeDeserializer(ctxt);
            if (_unwrapRoot) {
                resultNode = (JsonNode) _unwrapAndDeserialize(p, ctxt, JSON_NODE_TYPE, deser);
            } else {
                resultNode = (JsonNode) deser.deserialize(p, ctxt);
                if (_config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
                    _verifyNoTrailingTokens(p, ctxt, JSON_NODE_TYPE);
                }
            }
        }
        return resultNode;
    }
    public <T extends TreeNode> T readTree(JsonParser p) throws IOException {
        return (T) _bindAsTreeOrNull(p);
    }
}
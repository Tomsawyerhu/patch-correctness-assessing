public class test {
    protected JsonNode _bindAsTree(JsonParser jp) throws IOException
    {
        JsonNode result;
        JsonToken t = _initForReading(jp);
        if (t == JsonToken.VALUE_NULL || t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
            result = NullNode.instance;
        } else {
            DeserializationContext ctxt = createDeserializationContext(jp, _config);
            JsonDeserializer<Object> deser = _findTreeDeserializer(ctxt);
            if (_unwrapRoot) {
                result = (JsonNode) _unwrapAndDeserialize(jp, ctxt, JSON_NODE_TYPE, deser);
            } else {
                result = (JsonNode) deser.deserialize(jp, ctxt);
            }
        }
        // Need to consume the token too
        jp.clearCurrentToken();
        return result;
    }
    protected JsonDeserializer<Object> _findTreeDeserializer(DeserializationContext ctxt)
        throws JsonMappingException
    {
        JsonDeserializer<Object> deser = _rootDeserializers.get(JSON_NODE_TYPE);
        if (deser == null) {
            // Nope: need to ask provider to resolve it
            deser = ctxt.findRootValueDeserializer(JSON_NODE_TYPE);
            if (deser == null) { // can this happen?
                throw new JsonMappingException("Can not find a deserializer for type "+JSON_NODE_TYPE);
            }
            _rootDeserializers.put(JSON_NODE_TYPE, deser);
        }
        return deser;
    }
}
public class test {
    protected final Object _deserialize(JsonParser p, DeserializationContext ctxt,
            int index, String typeId) throws IOException
    {
        JsonParser p2 = _tokens[index].asParser(p);
        JsonToken t = p2.nextToken();
        // 29-Sep-2015, tatu: As per [databind#942], nulls need special support

        TokenBuffer merged = new TokenBuffer(p);
        merged.writeStartArray();
        merged.writeString(typeId);
        merged.copyCurrentStructure(p2);
        merged.writeEndArray();

        // needs to point to START_OBJECT (or whatever first token is)
        JsonParser mp = merged.asParser(p);
        mp.nextToken();
        return _properties[index].getProperty().deserialize(mp, ctxt);
    }
    protected final void _deserializeAndSet(JsonParser p, DeserializationContext ctxt,
            Object bean, int index, String typeId) throws IOException
    {
        /* Ok: time to mix type id, value; and we will actually use "wrapper-array"
         * style to ensure we can handle all kinds of JSON constructs.
         */
        JsonParser p2 = _tokens[index].asParser(p);
        JsonToken t = p2.nextToken();
        // 29-Sep-2015, tatu: As per [databind#942], nulls need special support
        TokenBuffer merged = new TokenBuffer(p);
        merged.writeStartArray();
        merged.writeString(typeId);
        
        merged.copyCurrentStructure(p2);
        merged.writeEndArray();
        // needs to point to START_OBJECT (or whatever first token is)
        JsonParser mp = merged.asParser(p);
        mp.nextToken();
        _properties[index].getProperty().deserializeAndSet(mp, ctxt, bean);
    }
}
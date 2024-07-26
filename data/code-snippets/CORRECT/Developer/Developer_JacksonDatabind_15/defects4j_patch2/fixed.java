public class test {
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
        throws JsonMappingException
    {
        /* 03-Sep-2012, tatu: Not sure if this can be made to really work
         *    properly... but for now, try this:
         */
        // 02-Apr-2015, tatu: For dynamic case, very little we can do
        if (_delegateSerializer != null) {
            _delegateSerializer.acceptJsonFormatVisitor(visitor, typeHint);
        }
    }
    public void serializeWithType(Object value, JsonGenerator gen, SerializerProvider provider,
            TypeSerializer typeSer) throws IOException
    {
        /* 03-Oct-2012, tatu: This is actually unlikely to work ok... but for now,
         *    let's give it a chance?
         */
        Object delegateValue = convertValue(value);
        JsonSerializer<Object> ser = _delegateSerializer;
        if (ser == null) {
            ser = _findSerializer(value, provider);
        }
        ser.serializeWithType(delegateValue, gen, provider, typeSer);
    }
    protected JsonSerializer<Object> _findSerializer(Object value, SerializerProvider serializers)
        throws JsonMappingException
    {
        // NOTE: will NOT call contextualization
        return serializers.findValueSerializer(value.getClass());
    }
    public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property)
        throws JsonMappingException
    {
        JsonSerializer<?> delSer = _delegateSerializer;
        JavaType delegateType = _delegateType;

        if (delSer == null) {
            // Otherwise, need to locate serializer to delegate to. For that we need type information...
            if (delegateType == null) {
                delegateType = _converter.getOutputType(provider.getTypeFactory());
            }
            /* 02-Apr-2015, tatu: For "dynamic case", where type is only specified as
             *    java.lang.Object (or missing generic), [databind#731]
             */
            if (!delegateType.isJavaLangObject()) {
                delSer = provider.findValueSerializer(delegateType);
            }
        }
        if (delSer instanceof ContextualSerializer) {
            delSer = provider.handleSecondaryContextualization(delSer, property);
        }
        if (delSer == _delegateSerializer && delegateType == _delegateType) {
            return this;
        }
        return withDelegate(_converter, delegateType, delSer);
    }
    public boolean isEmpty(SerializerProvider prov, Object value)
    {
        Object delegateValue = convertValue(value);
        if (_delegateSerializer == null) { // best we can do for now, too costly to look up
            return (value == null);
        }
        return _delegateSerializer.isEmpty(prov, delegateValue);
    }
    public boolean isEmpty(Object value)
    {
        Object delegateValue = convertValue(value);
        if (_delegateSerializer == null) { // best we can do for now, too costly to look up
            return (value == null);
        }
        return _delegateSerializer.isEmpty(delegateValue);
    }
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException
    {
        Object delegateValue = convertValue(value);
        // should we accept nulls?
        if (delegateValue == null) {
            provider.defaultSerializeNull(gen);
            return;
        }
        // 02-Apr-2015, tatu: As per [databind#731] may need to do dynamic lookup
        JsonSerializer<Object> ser = _delegateSerializer;
        if (ser == null) {
            ser = _findSerializer(delegateValue, provider);
        }
        ser.serialize(delegateValue, gen, provider);
    }
}
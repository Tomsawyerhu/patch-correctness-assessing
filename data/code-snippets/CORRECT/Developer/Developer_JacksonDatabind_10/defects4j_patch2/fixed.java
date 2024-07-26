public class test {
    public void getAndSerialize(Object bean, JsonGenerator gen, SerializerProvider provider)
        throws Exception
    {
        Object value = _accessor.getValue(bean);
        if (value == null) {
            return;
        }
        if (!(value instanceof Map<?,?>)) {
            throw new JsonMappingException("Value returned by 'any-getter' ("
                    +_accessor.getName()+"()) not java.util.Map but "+value.getClass().getName());
        }
        // 23-Feb-2015, tatu: Nasty, but has to do (for now)
        if (_mapSerializer != null) {
            _mapSerializer.serializeFields((Map<?,?>) value, gen, provider);
            return;
        }
        _serializer.serialize(value, gen, provider);
    }
    public void resolve(SerializerProvider provider) throws JsonMappingException
    {
        // 05-Sep-2013, tatu: I _think_ this can be considered a primary property...
        if (_serializer instanceof ContextualSerializer) {
            JsonSerializer<?> ser = provider.handlePrimaryContextualization(_serializer, _property);
            _serializer = (JsonSerializer<Object>) ser;
            if (ser instanceof MapSerializer) {
                _mapSerializer = (MapSerializer) ser;
            }
        }
    }
    public void getAndFilter(Object bean, JsonGenerator gen, SerializerProvider provider,
            PropertyFilter filter)
                    throws Exception
    {
        Object value = _accessor.getValue(bean);
        if (value == null) {
            return;
        }
        if (!(value instanceof Map<?,?>)) {
            throw new JsonMappingException("Value returned by 'any-getter' ("
                    +_accessor.getName()+"()) not java.util.Map but "+value.getClass().getName());
        }
        // 19-Oct-2014, tatu: Should we try to support @JsonInclude options here?
        if (_mapSerializer != null) {
            _mapSerializer.serializeFilteredFields((Map<?,?>) value, gen, provider, filter, null);
            return;
        }
        // ... not sure how custom handler would do it
        _serializer.serialize(value, gen, provider);
    }
    public AnyGetterWriter(BeanProperty property,
            AnnotatedMember accessor, JsonSerializer<?> serializer)
    {
        _accessor = accessor;
        _property = property;
        _serializer = (JsonSerializer<Object>) serializer;
        if (serializer instanceof MapSerializer) {
            _mapSerializer = (MapSerializer) serializer;
        }
    }
}
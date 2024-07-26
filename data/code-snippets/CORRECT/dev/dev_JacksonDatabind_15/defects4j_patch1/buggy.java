public class test {
    public JsonSerializer<Object> createSerializer(SerializerProvider prov,
            JavaType origType)
        throws JsonMappingException
    {
        // Very first thing, let's check if there is explicit serializer annotation:
        final SerializationConfig config = prov.getConfig();
        BeanDescription beanDesc = config.introspect(origType);
        JsonSerializer<?> ser = findSerializerFromAnnotation(prov, beanDesc.getClassInfo());
        if (ser != null) {
            return (JsonSerializer<Object>) ser;
        }
        boolean staticTyping;
        // Next: we may have annotations that further define types to use...
        JavaType type = modifyTypeByAnnotation(config, beanDesc.getClassInfo(), origType);
        if (type == origType) { // no changes, won't force static typing
            staticTyping = false;
        } else { // changes; assume static typing; plus, need to re-introspect if class differs
            staticTyping = true;
            if (!type.hasRawClass(origType.getRawClass())) {
                beanDesc = config.introspect(type);
            }
        }
        // Slight detour: do we have a Converter to consider?
        Converter<Object,Object> conv = beanDesc.findSerializationConverter();
        if (conv == null) { // no, simple
            return (JsonSerializer<Object>) _createSerializer2(prov, type, beanDesc, staticTyping);
        }
        JavaType delegateType = conv.getOutputType(prov.getTypeFactory());
        
        // One more twist, as per [Issue#288]; probably need to get new BeanDesc
        if (!delegateType.hasRawClass(type.getRawClass())) {
            beanDesc = config.introspect(delegateType);
            // [#359]: explicitly check (again) for @JsonSerializer...
            ser = findSerializerFromAnnotation(prov, beanDesc.getClassInfo());
        }
        // [databind#731]: Should skip if nominally java.lang.Object
        if (ser == null) {
            ser = _createSerializer2(prov, delegateType, beanDesc, true);
        }
        return new StdDelegatingSerializer(conv, delegateType, ser);
    }
    protected JsonSerializer<Object> findConvertingSerializer(SerializerProvider provider,
            BeanPropertyWriter prop)
        throws JsonMappingException
    {
        final AnnotationIntrospector intr = provider.getAnnotationIntrospector();
        if (intr != null) {
            AnnotatedMember m = prop.getMember();
            if (m != null) {
                Object convDef = intr.findSerializationConverter(m);
                if (convDef != null) {
                    Converter<Object,Object> conv = provider.converterInstance(prop.getMember(), convDef);
                    JavaType delegateType = conv.getOutputType(provider.getTypeFactory());
                    // [databind#731]: Should skip if nominally java.lang.Object
                    JsonSerializer<?> ser = provider.findValueSerializer(delegateType, prop);
                    return new StdDelegatingSerializer(conv, delegateType, ser);
                }
            }
        }
        return null;
    }
    public boolean isEmpty(SerializerProvider prov, Object value)
    {
        Object delegateValue = convertValue(value);
        return _delegateSerializer.isEmpty(prov, delegateValue);
    }
    public void serializeWithType(Object value, JsonGenerator gen, SerializerProvider provider,
            TypeSerializer typeSer) throws IOException
    {
        /* 03-Oct-2012, tatu: This is actually unlikely to work ok... but for now,
         *    let's give it a chance?
         */
        Object delegateValue = convertValue(value);
        _delegateSerializer.serializeWithType(delegateValue, gen, provider, typeSer);
    }
    public boolean isEmpty(Object value)
    {
        Object delegateValue = convertValue(value);
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
        _delegateSerializer.serialize(delegateValue, gen, provider);
    }
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
        throws JsonMappingException
    {
        /* 03-Sep-2012, tatu: Not sure if this can be made to really work
         *    properly... but for now, try this:
         */
        // 02-Apr-2015, tatu: For dynamic case, very little we can do
        _delegateSerializer.acceptJsonFormatVisitor(visitor, typeHint);
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
                delSer = provider.findValueSerializer(delegateType);
        }
        if (delSer instanceof ContextualSerializer) {
            delSer = provider.handleSecondaryContextualization(delSer, property);
        }
        return (delSer == _delegateSerializer) ? this
                : withDelegate(_converter, delegateType, delSer);
    }
    protected JsonSerializer<?> findConvertingContentSerializer(SerializerProvider provider,
            BeanProperty prop, JsonSerializer<?> existingSerializer)
        throws JsonMappingException
    {
        /* 19-Oct-2014, tatu: As per [databind#357], need to avoid infinite loop
         *   when applying contextual content converter; this is not ideal way,
         *   but should work for most cases.
         */
        final AnnotationIntrospector intr = provider.getAnnotationIntrospector();
        if (intr != null && prop != null) {
            AnnotatedMember m = prop.getMember();
            if (m != null) {
                Object convDef = intr.findSerializationContentConverter(m);
                if (convDef != null) {
                    Converter<Object,Object> conv = provider.converterInstance(prop.getMember(), convDef);
                    JavaType delegateType = conv.getOutputType(provider.getTypeFactory());
                    // [databind#731]: Should skip if nominally java.lang.Object
                    if (existingSerializer == null) {
                        existingSerializer = provider.findValueSerializer(delegateType);
                    }
                    return new StdDelegatingSerializer(conv, delegateType, existingSerializer);
                }
            }
        }
        return existingSerializer;
    }
}
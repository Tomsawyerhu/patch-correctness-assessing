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
        if (ser == null && !delegateType.isJavaLangObject()) {
            ser = _createSerializer2(prov, delegateType, beanDesc, true);
        }
        return new StdDelegatingSerializer(conv, delegateType, ser);
    }
}
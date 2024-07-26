public class test {
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
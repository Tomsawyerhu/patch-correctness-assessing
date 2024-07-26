public class test {
    protected static Boolean _isShapeWrittenUsingIndex(Class<?> enumClass,
            JsonFormat.Value format, boolean fromClass,
            Boolean defaultValue)
    {
        JsonFormat.Shape shape = (format == null) ? null : format.getShape();
        if (shape == null) {
            return defaultValue;
        }
        // i.e. "default", check dynamically
        if (shape == Shape.ANY || shape == Shape.SCALAR) {
            return defaultValue;
        }
        // 19-May-2016, tatu: also consider "natural" shape
        if (shape == Shape.STRING || shape == Shape.NATURAL) {
            return Boolean.FALSE;
        }
        // 01-Oct-2014, tatu: For convenience, consider "as-array" to also mean 'yes, use index')
        if (shape.isNumeric() || (shape == Shape.ARRAY)) {
            return Boolean.TRUE;
        }
        // 07-Mar-2017, tatu: Also means `OBJECT` not available as property annotation...
        throw new IllegalArgumentException(String.format(
                "Unsupported serialization shape (%s) for Enum %s, not supported as %s annotation",
                    shape, enumClass.getName(), (fromClass? "class" : "property")));
    }
    public JsonSerializer<?> createContextual(SerializerProvider serializers,
            BeanProperty property) throws JsonMappingException
    {
        if (property != null) {
            JsonFormat.Value format = findFormatOverrides(serializers,
                    property, handledType());
            if (format != null) {
                Boolean serializeAsIndex = _isShapeWrittenUsingIndex(property.getType().getRawClass(),
                        format, false, _serializeAsIndex);
                if (serializeAsIndex != _serializeAsIndex) {
                    return new EnumSerializer(_values, serializeAsIndex);
                }
            }
        }
        return this;
    }
    public static EnumSerializer construct(Class<?> enumClass, SerializationConfig config,
            BeanDescription beanDesc, JsonFormat.Value format)
    {
        /* 08-Apr-2015, tatu: As per [databind#749], we can not statically determine
         *   between name() and toString(), need to construct `EnumValues` with names,
         *   handle toString() case dynamically (for example)
         */
        EnumValues v = EnumValues.constructFromName(config, (Class<Enum<?>>) enumClass);
        Boolean serializeAsIndex = _isShapeWrittenUsingIndex(enumClass, format, true, null);
        return new EnumSerializer(v, serializeAsIndex);
    }
}
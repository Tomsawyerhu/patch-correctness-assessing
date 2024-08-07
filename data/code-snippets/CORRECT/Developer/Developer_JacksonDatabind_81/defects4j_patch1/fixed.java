public class test {
    public JavaType refineSerializationType(final MapperConfig<?> config,
            final Annotated a, final JavaType baseType) throws JsonMappingException
    {
        JavaType type = baseType;
        final TypeFactory tf = config.getTypeFactory();

        final JsonSerialize jsonSer = _findAnnotation(a, JsonSerialize.class);
        
        // Ok: start by refining the main type itself; common to all types

        final Class<?> serClass = (jsonSer == null) ? null : _classIfExplicit(jsonSer.as());
        if (serClass != null) {
            if (type.hasRawClass(serClass)) {
                // 30-Nov-2015, tatu: As per [databind#1023], need to allow forcing of
                //    static typing this way
                type = type.withStaticTyping();
            } else {
                Class<?> currRaw = type.getRawClass();
                try {
                    // 11-Oct-2015, tatu: For deser, we call `TypeFactory.constructSpecializedType()`,
                    //   may be needed here too in future?
                    if (serClass.isAssignableFrom(currRaw)) { // common case
                        type = tf.constructGeneralizedType(type, serClass);
                    } else if (currRaw.isAssignableFrom(serClass)) { // specialization, ok as well
                        type = tf.constructSpecializedType(type, serClass);
                    } else if (_primitiveAndWrapper(currRaw, serClass)) {
                        // 27-Apr-2017, tatu: [databind#1592] ignore primitive<->wrapper refinements
                        type = type.withStaticTyping();
                    } else {
                        throw new JsonMappingException(null,
                                String.format("Can not refine serialization type %s into %s; types not related",
                                        type, serClass.getName()));
                    }
                } catch (IllegalArgumentException iae) {
                    throw new JsonMappingException(null,
                            String.format("Failed to widen type %s with annotation (value %s), from '%s': %s",
                                    type, serClass.getName(), a.getName(), iae.getMessage()),
                                    iae);
                }
            }
        }
        // Then further processing for container types

        // First, key type (for Maps, Map-like types):
        if (type.isMapLikeType()) {
            JavaType keyType = type.getKeyType();
            final Class<?> keyClass = (jsonSer == null) ? null : _classIfExplicit(jsonSer.keyAs());
            if (keyClass != null) {
                if (keyType.hasRawClass(keyClass)) {
                    keyType = keyType.withStaticTyping();
                } else {
                    Class<?> currRaw = keyType.getRawClass();
                    try {
                        // 19-May-2016, tatu: As per [databind#1231], [databind#1178] may need to actually
                        //   specialize (narrow) type sometimes, even if more commonly opposite
                        //   is needed.
                        if (keyClass.isAssignableFrom(currRaw)) { // common case
                            keyType = tf.constructGeneralizedType(keyType, keyClass);
                        } else if (currRaw.isAssignableFrom(keyClass)) { // specialization, ok as well
                            keyType = tf.constructSpecializedType(keyType, keyClass);
                        } else if (_primitiveAndWrapper(currRaw, keyClass)) {
                            // 27-Apr-2017, tatu: [databind#1592] ignore primitive<->wrapper refinements
                            keyType = keyType.withStaticTyping();
                        } else {
                            throw new JsonMappingException(null,
                                    String.format("Can not refine serialization key type %s into %s; types not related",
                                            keyType, keyClass.getName()));
                        }
                    } catch (IllegalArgumentException iae) {
                        throw new JsonMappingException(null,
                                String.format("Failed to widen key type of %s with concrete-type annotation (value %s), from '%s': %s",
                                        type, keyClass.getName(), a.getName(), iae.getMessage()),
                                        iae);
                    }
                }
                type = ((MapLikeType) type).withKeyType(keyType);
            }
        }

        JavaType contentType = type.getContentType();
        if (contentType != null) { // collection[like], map[like], array, reference
            // And then value types for all containers:
           final Class<?> contentClass = (jsonSer == null) ? null : _classIfExplicit(jsonSer.contentAs());
           if (contentClass != null) {
               if (contentType.hasRawClass(contentClass)) {
                   contentType = contentType.withStaticTyping();
               } else {
                   // 03-Apr-2016, tatu: As per [databind#1178], may need to actually
                   //   specialize (narrow) type sometimes, even if more commonly opposite
                   //   is needed.
                   Class<?> currRaw = contentType.getRawClass();
                   try {
                       if (contentClass.isAssignableFrom(currRaw)) { // common case
                           contentType = tf.constructGeneralizedType(contentType, contentClass);
                       } else if (currRaw.isAssignableFrom(contentClass)) { // specialization, ok as well
                           contentType = tf.constructSpecializedType(contentType, contentClass);
                       } else if (_primitiveAndWrapper(currRaw, contentClass)) {
                           // 27-Apr-2017, tatu: [databind#1592] ignore primitive<->wrapper refinements
                           contentType = contentType.withStaticTyping();
                       } else {
                           throw new JsonMappingException(null,
                                   String.format("Can not refine serialization content type %s into %s; types not related",
                                           contentType, contentClass.getName()));
                       }
                   } catch (IllegalArgumentException iae) { // shouldn't really happen
                       throw new JsonMappingException(null,
                               String.format("Internal error: failed to refine value type of %s with concrete-type annotation (value %s), from '%s': %s",
                                       type, contentClass.getName(), a.getName(), iae.getMessage()),
                                       iae);
                   }
               }
               type = type.withContentType(contentType);
           }
        }
        return type;
    }
    private boolean _primitiveAndWrapper(Class<?> baseType, Class<?> refinement)
    {
        if (baseType.isPrimitive()) {
            return baseType == ClassUtil.primitiveType(refinement);
        }
        if (refinement.isPrimitive()) {
            return refinement == ClassUtil.primitiveType(baseType);
        }
        return false;
    }
    public JavaType refineDeserializationType(final MapperConfig<?> config,
            final Annotated a, final JavaType baseType) throws JsonMappingException
    {
        JavaType type = baseType;
        final TypeFactory tf = config.getTypeFactory();

        final JsonDeserialize jsonDeser = _findAnnotation(a, JsonDeserialize.class);
        
        // Ok: start by refining the main type itself; common to all types
        final Class<?> valueClass = (jsonDeser == null) ? null : _classIfExplicit(jsonDeser.as());
        if ((valueClass != null) && !type.hasRawClass(valueClass)
                && !_primitiveAndWrapper(type, valueClass)) {
            try {
                type = tf.constructSpecializedType(type, valueClass);
            } catch (IllegalArgumentException iae) {
                throw new JsonMappingException(null,
                        String.format("Failed to narrow type %s with annotation (value %s), from '%s': %s",
                                type, valueClass.getName(), a.getName(), iae.getMessage()),
                                iae);
            }
        }
        // Then further processing for container types

        // First, key type (for Maps, Map-like types):
        if (type.isMapLikeType()) {
            JavaType keyType = type.getKeyType();
            final Class<?> keyClass = (jsonDeser == null) ? null : _classIfExplicit(jsonDeser.keyAs());
            if ((keyClass != null)
                    && !_primitiveAndWrapper(keyType, keyClass)) {
                try {
                    keyType = tf.constructSpecializedType(keyType, keyClass);
                    type = ((MapLikeType) type).withKeyType(keyType);
                } catch (IllegalArgumentException iae) {
                    throw new JsonMappingException(null,
                            String.format("Failed to narrow key type of %s with concrete-type annotation (value %s), from '%s': %s",
                                    type, keyClass.getName(), a.getName(), iae.getMessage()),
                                    iae);
                }
            }
        }
        JavaType contentType = type.getContentType();
        if (contentType != null) { // collection[like], map[like], array, reference
            // And then value types for all containers:
            final Class<?> contentClass = (jsonDeser == null) ? null : _classIfExplicit(jsonDeser.contentAs());
            if ((contentClass != null)
                    && !_primitiveAndWrapper(contentType, contentClass)) {
                try {
                    contentType = tf.constructSpecializedType(contentType, contentClass);
                    type = type.withContentType(contentType);
                } catch (IllegalArgumentException iae) {
                    throw new JsonMappingException(null,
                            String.format("Failed to narrow value type of %s with concrete-type annotation (value %s), from '%s': %s",
                                    type, contentClass.getName(), a.getName(), iae.getMessage()),
                            iae);
                }
            }
        }
        return type;
    }
    private boolean _primitiveAndWrapper(JavaType baseType, Class<?> refinement)
    {
        if (baseType.isPrimitive()) {
            return baseType.hasRawClass(ClassUtil.primitiveType(refinement));
        }
        if (refinement.isPrimitive()) {
            return refinement == ClassUtil.primitiveType(baseType.getRawClass());
        }
        return false;
    }
}
public class test {
    protected <T extends JavaType> T modifyTypeByAnnotation(DeserializationContext ctxt,
            Annotated a, T type)
        throws JsonMappingException
    {
        // first: let's check class for the instance itself:
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        Class<?> subclass = intr.findDeserializationType(a, type);
        if (subclass != null) {
            try {
                type = (T) ctxt.getTypeFactory().constructSpecializedType(type, subclass);
            } catch (IllegalArgumentException iae) {
                throw new JsonMappingException("Failed to narrow type "+type+" with concrete-type annotation (value "+subclass.getName()+"), method '"+a.getName()+"': "+iae.getMessage(), null, iae);
            }
        }

        // then key class
        if (type.isContainerType()) {
            Class<?> keyClass = intr.findDeserializationKeyType(a, type.getKeyType());
            if (keyClass != null) {
                // illegal to use on non-Maps
                if (!(type instanceof MapLikeType)) {
                    throw new JsonMappingException("Illegal key-type annotation: type "+type+" is not a Map(-like) type");
                }
                try {
                    type = (T) ((MapLikeType) type).narrowKey(keyClass);
                } catch (IllegalArgumentException iae) {
                    throw new JsonMappingException("Failed to narrow key type "+type+" with key-type annotation ("+keyClass.getName()+"): "+iae.getMessage(), null, iae);
                }
            }
            JavaType keyType = type.getKeyType();
            /* 21-Mar-2011, tatu: ... and associated deserializer too (unless already assigned)
             *   (not 100% why or how, but this does seem to get called more than once, which
             *   is not good: for now, let's just avoid errors)
             */
            if (keyType != null && keyType.getValueHandler() == null) {
                Object kdDef = intr.findKeyDeserializer(a);
                KeyDeserializer kd = ctxt.keyDeserializerInstance(a, kdDef);
                if (kd != null) {
                    type = (T) ((MapLikeType) type).withKeyValueHandler(kd);
                    keyType = type.getKeyType(); // just in case it's used below
                }
            }            
           
           // and finally content class; only applicable to structured types
           Class<?> cc = intr.findDeserializationContentType(a, type.getContentType());
           if (cc != null) {
               try {
                   type = (T) type.narrowContentsBy(cc);
               } catch (IllegalArgumentException iae) {
                   throw new JsonMappingException("Failed to narrow content type "+type+" with content-type annotation ("+cc.getName()+"): "+iae.getMessage(), null, iae);
               }
           }
           // ... as well as deserializer for contents:
           JavaType contentType = type.getContentType();
           if (contentType.getValueHandler() == null) { // as with above, avoid resetting (which would trigger exception)
               Object cdDef = intr.findContentDeserializer(a);
                JsonDeserializer<?> cd = ctxt.deserializerInstance(a, cdDef);
                if (cd != null) {
                    type = (T) type.withContentValueHandler(cd);
                }
            }
        }
        return type;
    }
    private JavaType modifyTypeByAnnotation(DeserializationContext ctxt,
            Annotated a, JavaType type)
        throws JsonMappingException
    {
        // first: let's check class for the instance itself:
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        Class<?> subclass = intr.findDeserializationType(a, type);
        if (subclass != null) {
            try {
                type = ctxt.getTypeFactory().constructSpecializedType(type, subclass);
            } catch (IllegalArgumentException iae) {
                throw new JsonMappingException("Failed to narrow type "+type+" with concrete-type annotation (value "+subclass.getName()+"), method '"+a.getName()+"': "+iae.getMessage(), null, iae);
            }
        }

        // then key class
        if (type.isContainerType()) {
            Class<?> keyClass = intr.findDeserializationKeyType(a, type.getKeyType());
            if (keyClass != null) {
                // illegal to use on non-Maps
                if (!(type instanceof MapLikeType)) {
                    throw new JsonMappingException("Illegal key-type annotation: type "+type+" is not a Map(-like) type");
                }
                try {
                    type = ((MapLikeType) type).narrowKey(keyClass);
                } catch (IllegalArgumentException iae) {
                    throw new JsonMappingException("Failed to narrow key type "+type+" with key-type annotation ("+keyClass.getName()+"): "+iae.getMessage(), null, iae);
                }
            }
            JavaType keyType = type.getKeyType();
            /* 21-Mar-2011, tatu: ... and associated deserializer too (unless already assigned)
             *   (not 100% why or how, but this does seem to get called more than once, which
             *   is not good: for now, let's just avoid errors)
             */
            if (keyType != null && keyType.getValueHandler() == null) {
                Object kdDef = intr.findKeyDeserializer(a);
                if (kdDef != null) {
                    KeyDeserializer kd = ctxt.keyDeserializerInstance(a, kdDef);
                    if (kd != null) {
                        type = ((MapLikeType) type).withKeyValueHandler(kd);
                        keyType = type.getKeyType(); // just in case it's used below
                    }
                }
            }            
            
            // and finally content class; only applicable to structured types
            Class<?> cc = intr.findDeserializationContentType(a, type.getContentType());
            if (cc != null) {
                try {
                    type = type.narrowContentsBy(cc);
                } catch (IllegalArgumentException iae) {
                    throw new JsonMappingException("Failed to narrow content type "+type+" with content-type annotation ("+cc.getName()+"): "+iae.getMessage(), null, iae);
                }
            }
            // ... as well as deserializer for contents:
            JavaType contentType = type.getContentType();
            if (contentType.getValueHandler() == null) { // as with above, avoid resetting (which would trigger exception)
                Object cdDef = intr.findContentDeserializer(a);
                if (cdDef != null) {
                    JsonDeserializer<?> cd = null;
                    if (cdDef instanceof JsonDeserializer<?>) {
                        cdDef = (JsonDeserializer<?>) cdDef;
                    } else {
                        Class<?> cdClass = _verifyAsClass(cdDef, "findContentDeserializer", JsonDeserializer.None.class);
                        if (cdClass != null) {
                            cd = ctxt.deserializerInstance(a, cdClass);
                        }
                    }
                    if (cd != null) {
                        type = type.withContentValueHandler(cd);
                    }
                }
            }
        }
        return type;
    }
    protected final JsonDeserializer<Object> _findDeserializer(DeserializationContext ctxt,
            String typeId) throws IOException
    {
        JsonDeserializer<Object> deser = _deserializers.get(typeId);
        if (deser == null) {
            /* As per [Databind#305], need to provide contextual info. But for
             * backwards compatibility, let's start by only supporting this
             * for base class, not via interface. Later on we can add this
             * to the interface, assuming deprecation at base class helps.
             */
            JavaType type = _idResolver.typeFromId(ctxt, typeId);
            if (type == null) {
                // As per [JACKSON-614], use the default impl if no type id available:
                deser = _findDefaultImplDeserializer(ctxt);
                if (deser == null) {
                    deser = _handleUnknownTypeId(ctxt, typeId, _idResolver, _baseType);
                }
            } else {
                /* 16-Dec-2010, tatu: Since nominal type we get here has no (generic) type parameters,
                 *   we actually now need to explicitly narrow from base type (which may have parameterization)
                 *   using raw type.
                 *
                 *   One complication, though; can not change 'type class' (simple type to container); otherwise
                 *   we may try to narrow a SimpleType (Object.class) into MapType (Map.class), losing actual
                 *   type in process (getting SimpleType of Map.class which will not work as expected)
                 */
                if ((_baseType != null)
                        && _baseType.getClass() == type.getClass()) {
                    /* 09-Aug-2015, tatu: Not sure if the second part of the check makes sense;
                     *   but it appears to check that JavaType impl class is the same which is
                     *   important for some reason?
                     *   Disabling the check will break 2 Enum-related tests.
                     */
                    type = ctxt.getTypeFactory().constructSpecializedType(_baseType, type.getRawClass());
                }
                deser = ctxt.findContextualValueDeserializer(type, _property);
            }
            _deserializers.put(typeId, deser);
        }
        return deser;
    }
    public JavaType findTypeMapping(DeserializationConfig config, JavaType type)
    {
        // this is the main mapping base, so let's 
        Class<?> src = type.getRawClass();
        Class<?> dst = _mappings.get(new ClassKey(src));
        if (dst == null) {
            return null;
        }
        // 09-Aug-2015, tatu: Instead of direct call via JavaType, better use TypeFactory
        return config.getTypeFactory().constructSpecializedType(type, dst);
    }
}
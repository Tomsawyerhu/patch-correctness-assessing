public class test {
    public ObjectIdInfo findObjectReferenceInfo(Annotated ann, ObjectIdInfo objectIdInfo) {
        JsonIdentityReference ref = _findAnnotation(ann, JsonIdentityReference.class);
        if (ref != null) {
            objectIdInfo = objectIdInfo.withAlwaysAsId(ref.alwaysAsId());
        }
        return objectIdInfo;
    }
    public JsonSerializer<?> createContextual(SerializerProvider provider,
            BeanProperty property)
        throws JsonMappingException
    {
        final AnnotationIntrospector intr = provider.getAnnotationIntrospector();
        final AnnotatedMember accessor = (property == null || intr == null)
                ? null : property.getMember();
        final SerializationConfig config = provider.getConfig();
        
        // Let's start with one big transmutation: Enums that are annotated
        // to serialize as Objects may want to revert
        JsonFormat.Shape shape = null;
        if (accessor != null) {
            JsonFormat.Value format = intr.findFormat((Annotated) accessor);

            if (format != null) {
                shape = format.getShape();
                // or, alternatively, asked to revert "back to" other representations...
                if (shape != _serializationShape) {
                    if (_handledType.isEnum()) {
                        switch (shape) {
                        case STRING:
                        case NUMBER:
                        case NUMBER_INT:
                            // 12-Oct-2014, tatu: May need to introspect full annotations... but
                            //   for now, just do class ones
                            BeanDescription desc = config.introspectClassAnnotations(_handledType);
                            JsonSerializer<?> ser = EnumSerializer.construct(_handledType,
                                    provider.getConfig(), desc, format);
                            return provider.handlePrimaryContextualization(ser, property);
                        }
                    }
                }
            }
        }

        ObjectIdWriter oiw = _objectIdWriter;
        String[] ignorals = null;
        Object newFilterId = null;
        
        // Then we may have an override for Object Id
        if (accessor != null) {
            ignorals = intr.findPropertiesToIgnore(accessor, true);
            ObjectIdInfo objectIdInfo = intr.findObjectIdInfo(accessor);
            if (objectIdInfo == null) {
                // no ObjectId override, but maybe ObjectIdRef?
                if (oiw != null) {
                    objectIdInfo = intr.findObjectReferenceInfo(accessor,
                            new ObjectIdInfo(NAME_FOR_OBJECT_REF, null, null, null));
                        oiw = _objectIdWriter.withAlwaysAsId(objectIdInfo.getAlwaysAsId());
                }
            } else {
                // Ugh: mostly copied from BeanDeserializerBase: but can't easily change it
                // to be able to move to SerializerProvider (where it really belongs)
                
                // 2.1: allow modifications by "id ref" annotations as well:
                objectIdInfo = intr.findObjectReferenceInfo(accessor, objectIdInfo);
                ObjectIdGenerator<?> gen;
                Class<?> implClass = objectIdInfo.getGeneratorType();
                JavaType type = provider.constructType(implClass);
                JavaType idType = provider.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
                // Property-based generator is trickier
                if (implClass == ObjectIdGenerators.PropertyGenerator.class) { // most special one, needs extra work
                    String propName = objectIdInfo.getPropertyName().getSimpleName();
                    BeanPropertyWriter idProp = null;

                    for (int i = 0, len = _props.length ;; ++i) {
                        if (i == len) {
                            throw new IllegalArgumentException("Invalid Object Id definition for "+_handledType.getName()
                                    +": can not find property with name '"+propName+"'");
                        }
                        BeanPropertyWriter prop = _props[i];
                        if (propName.equals(prop.getName())) {
                            idProp = prop;
                            /* Let's force it to be the first property to output
                             * (although it may still get rearranged etc)
                             */
                            if (i > 0) { // note: must shuffle both regular properties and filtered
                                System.arraycopy(_props, 0, _props, 1, i);
                                _props[0] = idProp;
                                if (_filteredProps != null) {
                                    BeanPropertyWriter fp = _filteredProps[i];
                                    System.arraycopy(_filteredProps, 0, _filteredProps, 1, i);
                                    _filteredProps[0] = fp;
                                }
                            }
                            break;
                        }
                    }
                    idType = idProp.getType();
                    gen = new PropertyBasedObjectIdGenerator(objectIdInfo, idProp);
                    oiw = ObjectIdWriter.construct(idType, (PropertyName) null, gen, objectIdInfo.getAlwaysAsId());
                } else { // other types need to be simpler
                    gen = provider.objectIdGeneratorInstance(accessor, objectIdInfo);
                    oiw = ObjectIdWriter.construct(idType, objectIdInfo.getPropertyName(), gen,
                            objectIdInfo.getAlwaysAsId());
                }
            }
            
            // Or change Filter Id in use?
            Object filterId = intr.findFilterId(accessor);
            if (filterId != null) {
                // but only consider case of adding a new filter id (no removal via annotation)
                if (_propertyFilterId == null || !filterId.equals(_propertyFilterId)) {
                    newFilterId = filterId;
                }
            }
        }
        // either way, need to resolve serializer:
        BeanSerializerBase contextual = this;
        if (oiw != null) {
            JsonSerializer<?> ser = provider.findValueSerializer(oiw.idType, property);
            oiw = oiw.withSerializer(ser);
            if (oiw != _objectIdWriter) {
                contextual = contextual.withObjectIdWriter(oiw);
            }
        }
        // And possibly add more properties to ignore
        if (ignorals != null && ignorals.length != 0) {
            contextual = contextual.withIgnorals(ignorals);
        }
        if (newFilterId != null) {
            contextual = contextual.withFilterId(newFilterId);
        }
        if (shape == null) {
            shape = _serializationShape;
        }
        if (shape == JsonFormat.Shape.ARRAY) {
            return contextual.asArraySerializer();
        }
        return contextual;
    }
}
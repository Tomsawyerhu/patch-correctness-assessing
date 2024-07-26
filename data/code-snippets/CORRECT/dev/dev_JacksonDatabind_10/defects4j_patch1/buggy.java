public class test {
    public void resolve(SerializerProvider provider) throws JsonMappingException
    {
        // 05-Sep-2013, tatu: I _think_ this can be considered a primary property...
        _mapSerializer = (MapSerializer) provider.handlePrimaryContextualization(_mapSerializer, _property);
    }
    public AnyGetterWriter(BeanProperty property,
            AnnotatedMember accessor, MapSerializer serializer)
    {
        _accessor = accessor;
        _property = property;
            _mapSerializer = (MapSerializer) serializer;
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
    }
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
    }
    protected JsonSerializer<Object> constructBeanSerializer(SerializerProvider prov,
            BeanDescription beanDesc)
        throws JsonMappingException
    {
        // 13-Oct-2010, tatu: quick sanity check: never try to create bean serializer for plain Object
        // 05-Jul-2012, tatu: ... but we should be able to just return "unknown type" serializer, right?
        if (beanDesc.getBeanClass() == Object.class) {
            return prov.getUnknownTypeSerializer(Object.class);
//            throw new IllegalArgumentException("Can not create bean serializer for Object.class");
        }
        final SerializationConfig config = prov.getConfig();
        BeanSerializerBuilder builder = constructBeanSerializerBuilder(beanDesc);
        builder.setConfig(config);

        // First: any detectable (auto-detect, annotations) properties to serialize?
        List<BeanPropertyWriter> props = findBeanProperties(prov, beanDesc, builder);
        if (props == null) {
            props = new ArrayList<BeanPropertyWriter>();
        }
        // [databind#638]: Allow injection of "virtual" properties:
        prov.getAnnotationIntrospector().findAndAddVirtualProperties(config, beanDesc.getClassInfo(), props);

        // [JACKSON-440] Need to allow modification bean properties to serialize:
        if (_factoryConfig.hasSerializerModifiers()) {
            for (BeanSerializerModifier mod : _factoryConfig.serializerModifiers()) {
                props = mod.changeProperties(config, beanDesc, props);
            }
        }

        // Any properties to suppress?
        props = filterBeanProperties(config, beanDesc, props);

        // [JACKSON-440] Need to allow reordering of properties to serialize
        if (_factoryConfig.hasSerializerModifiers()) {
            for (BeanSerializerModifier mod : _factoryConfig.serializerModifiers()) {
                props = mod.orderProperties(config, beanDesc, props);
            }
        }

        /* And if Object Id is needed, some preparation for that as well: better
         * do before view handling, mostly for the custom id case which needs
         * access to a property
         */
        builder.setObjectIdWriter(constructObjectIdHandler(prov, beanDesc, props));
        
        builder.setProperties(props);
        builder.setFilterId(findFilterId(config, beanDesc));
        
        AnnotatedMember anyGetter = beanDesc.findAnyGetter();
        if (anyGetter != null) {
            if (config.canOverrideAccessModifiers()) {
                anyGetter.fixAccess();
            }
            JavaType type = anyGetter.getType(beanDesc.bindingsForBeanType());
            // copied from BasicSerializerFactory.buildMapSerializer():
            boolean staticTyping = config.isEnabled(MapperFeature.USE_STATIC_TYPING);
            JavaType valueType = type.getContentType();
            TypeSerializer typeSer = createTypeSerializer(config, valueType);
            // last 2 nulls; don't know key, value serializers (yet)
            // 23-Feb-2015, tatu: As per [#705], need to support custom serializers
            MapSerializer anySer = MapSerializer.construct(null, type, staticTyping, typeSer, null, null, null);
                // TODO: support '@JsonIgnoreProperties' with any setter?
            // TODO: can we find full PropertyName?
            PropertyName name = new PropertyName(anyGetter.getName());
            BeanProperty.Std anyProp = new BeanProperty.Std(name, valueType, null,
                    beanDesc.getClassAnnotations(), anyGetter, PropertyMetadata.STD_OPTIONAL);
            builder.setAnyGetter(new AnyGetterWriter(anyProp, anyGetter, anySer));
        }
        // Next: need to gather view information, if any:
        processViews(config, builder);

        // Finally: let interested parties mess with the result bit more...
        if (_factoryConfig.hasSerializerModifiers()) {
            for (BeanSerializerModifier mod : _factoryConfig.serializerModifiers()) {
                builder = mod.updateBuilder(config, beanDesc, builder);
            }
        }
        
        JsonSerializer<Object> ser = (JsonSerializer<Object>) builder.build();
        
        if (ser == null) {
            // If we get this far, there were no properties found, so no regular BeanSerializer
            // would be constructed. But, couple of exceptions.
            // First: if there are known annotations, just create 'empty bean' serializer
            if (beanDesc.hasKnownClassAnnotations()) {
                return builder.createDummy();
            }
        }
        return ser;
    }
}
public class test {
        public void writeCustomTypePrefixForObject(Object value, JsonGenerator gen, String typeId) throws IOException {
            _typeSerializer.writeCustomTypePrefixForObject(_forObject, gen, typeId);
        }
        public void writeTypeSuffixForArray(Object value, JsonGenerator gen) throws IOException {
            _typeSerializer.writeTypeSuffixForArray(_forObject, gen);
        }
    public void serializeWithType(Object bean, JsonGenerator gen, SerializerProvider provider,
            TypeSerializer typeSer0) throws IOException
    {
        // Regardless of other parts, first need to find value to serialize:
        Object value = null;
        try {
            value = _accessorMethod.getValue(bean);
            // and if we got null, can also just write it directly
            if (value == null) {
                provider.defaultSerializeNull(gen);
                return;
            }
            JsonSerializer<Object> ser = _valueSerializer;
            if (ser == null) { // no serializer yet? Need to fetch
//                ser = provider.findTypedValueSerializer(value.getClass(), true, _property);
                ser = provider.findValueSerializer(value.getClass(), _property);
            } else {
                /* 09-Dec-2010, tatu: To work around natural type's refusal to add type info, we do
                 *    this (note: type is for the wrapper type, not enclosed value!)
                 */
                if (_forceTypeInformation) {
                    typeSer0.writeTypePrefixForScalar(bean, gen);
                    ser.serialize(value, gen, provider);
                    typeSer0.writeTypeSuffixForScalar(bean, gen);
                    return;
                }
            }
            // 28-Sep-2016, tatu: As per [databind#1385], we do need to do some juggling
            //    to use different Object for type id (logical type) and actual serialization
            //    (delegat type).
            TypeSerializerRerouter rr = new TypeSerializerRerouter(typeSer0, bean);
            ser.serializeWithType(value, gen, provider, rr);
        } catch (IOException ioe) {
            throw ioe;
        } catch (Exception e) {
            Throwable t = e;
            // Need to unwrap this specific type, to see infinite recursion...
            while (t instanceof InvocationTargetException && t.getCause() != null) {
                t = t.getCause();
            }
            // Errors shouldn't be wrapped (and often can't, as well)
            if (t instanceof Error) {
                throw (Error) t;
            }
            // let's try to indicate the path best we can...
            throw JsonMappingException.wrapWithPath(t, bean, _accessorMethod.getName() + "()");
        }
    }
        public void writeCustomTypePrefixForArray(Object value, JsonGenerator gen, String typeId) throws IOException {
            _typeSerializer.writeCustomTypePrefixForArray(_forObject, gen, typeId);
        }
        public TypeSerializer forProperty(BeanProperty prop) { // should never get called
            throw new UnsupportedOperationException();
        }
        public As getTypeInclusion() {
            return _typeSerializer.getTypeInclusion();
        }
        public void writeCustomTypeSuffixForScalar(Object value, JsonGenerator gen, String typeId) throws IOException {
            _typeSerializer.writeCustomTypeSuffixForScalar(_forObject, gen, typeId);
        }
        public void writeCustomTypeSuffixForArray(Object value, JsonGenerator gen, String typeId) throws IOException {
            _typeSerializer.writeCustomTypeSuffixForArray(_forObject, gen, typeId);
        }
        public String getPropertyName() {
            return _typeSerializer.getPropertyName();
        }
        public void writeCustomTypeSuffixForObject(Object value, JsonGenerator gen, String typeId) throws IOException {
            _typeSerializer.writeCustomTypeSuffixForObject(_forObject, gen, typeId);
        }
        public void writeCustomTypePrefixForScalar(Object value, JsonGenerator gen, String typeId)
                throws IOException {
            _typeSerializer.writeCustomTypePrefixForScalar(_forObject, gen, typeId);
        }
        public TypeIdResolver getTypeIdResolver() {
            return _typeSerializer.getTypeIdResolver();
        }
        public void writeTypePrefixForObject(Object value, JsonGenerator gen) throws IOException {
            _typeSerializer.writeTypePrefixForObject(_forObject, gen);
        }
        public TypeSerializerRerouter(TypeSerializer ts, Object ob) {
            _typeSerializer = ts;
            _forObject = ob;
        }
        public void writeTypePrefixForArray(Object value, JsonGenerator gen, Class<?> type) throws IOException {
            _typeSerializer.writeTypePrefixForArray(_forObject, gen, type);
        }
        public void writeTypePrefixForScalar(Object value, JsonGenerator gen) throws IOException {
            _typeSerializer.writeTypePrefixForScalar(_forObject, gen);
        }
        public void writeTypePrefixForArray(Object value, JsonGenerator gen) throws IOException {
            _typeSerializer.writeTypePrefixForArray(_forObject, gen);
        }
        public void writeTypeSuffixForScalar(Object value, JsonGenerator gen) throws IOException {
            _typeSerializer.writeTypeSuffixForScalar(_forObject, gen);
        }
        public void writeTypePrefixForObject(Object value, JsonGenerator gen, Class<?> type) throws IOException {
            _typeSerializer.writeTypePrefixForObject(_forObject, gen, type);
        }
        public void writeTypeSuffixForObject(Object value, JsonGenerator gen) throws IOException {
            _typeSerializer.writeTypeSuffixForObject(_forObject, gen);
        }
        public void writeTypePrefixForScalar(Object value, JsonGenerator gen, Class<?> type) throws IOException {
            _typeSerializer.writeTypePrefixForScalar(_forObject, gen, type);
        }
}
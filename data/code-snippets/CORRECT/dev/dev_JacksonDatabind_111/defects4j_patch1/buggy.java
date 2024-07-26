public class test {
    public SettableBeanProperty withValueDeserializer(JsonDeserializer<?> deser) {
        if (_valueDeserializer == deser) {
            return this;
        }
        // 07-May-2019, tatu: As per [databind#2303], must keep VD/NVP in-sync if they were
        return new CreatorProperty(this, deser, _nullProvider);
    }
    public SettableBeanProperty withValueDeserializer(JsonDeserializer<?> deser) {
        if (_valueDeserializer == deser) {
            return this;
        }
        // 07-May-2019, tatu: As per [databind#2303], must keep VD/NVP in-sync if they were
        return new FieldProperty(this, deser, _nullProvider);
    }
    public SettableBeanProperty withValueDeserializer(JsonDeserializer<?> deser) {
        if (_valueDeserializer == deser) {
            return this;
        }
        // 07-May-2019, tatu: As per [databind#2303], must keep VD/NVP in-sync if they were
        return new MethodProperty(this, deser, _nullProvider);
    }
    public SettableBeanProperty withValueDeserializer(JsonDeserializer<?> deser) {
        if (_valueDeserializer == deser) {
            return this;
        }
        // 07-May-2019, tatu: As per [databind#2303], must keep VD/NVP in-sync if they were
        return new ObjectIdReferenceProperty(this, deser, _nullProvider);
    }
    public SettableBeanProperty withValueDeserializer(JsonDeserializer<?> deser) {
        if (_valueDeserializer == deser) {
            return this;
        }
        // 07-May-2019, tatu: As per [databind#2303], must keep VD/NVP in-sync if they were
        return new ObjectIdValueProperty(this, deser, _nullProvider);
    }
    public SettableBeanProperty withValueDeserializer(JsonDeserializer<?> deser) {
        if (_valueDeserializer == deser) {
            return this;
        }
        // 07-May-2019, tatu: As per [databind#2303], must keep VD/NVP in-sync if they were
        return new SetterlessProperty(this, deser, _nullProvider);
    }
    public AtomicReference<Object> getNullValue(DeserializationContext ctxt) throws JsonMappingException {
        return new AtomicReference<Object>();
    }
}
public class test {
    public static JsonDeserializer<?> findForCollection(DeserializationContext ctxt,
            JavaType type)
        throws JsonMappingException
    {
        JavaUtilCollectionsConverter conv;

        // 10-Jan-2017, tatu: Some types from `java.util.Collections`/`java.util.Arrays` need bit of help...
        if (type.hasRawClass(CLASS_AS_ARRAYS_LIST)) {
            conv = converter(TYPE_AS_LIST, type, List.class);
        } else if (type.hasRawClass(CLASS_SINGLETON_LIST)) {
            conv = converter(TYPE_SINGLETON_LIST, type, List.class);
        } else if (type.hasRawClass(CLASS_SINGLETON_SET)) {
            conv = converter(TYPE_SINGLETON_SET, type, Set.class);
        // [databind#2265]: we may have another impl type for unmodifiable Lists, check both
        } else if (type.hasRawClass(CLASS_UNMODIFIABLE_LIST)) {
            conv = converter(TYPE_UNMODIFIABLE_LIST, type, List.class);
        } else if (type.hasRawClass(CLASS_UNMODIFIABLE_SET)) {
            conv = converter(TYPE_UNMODIFIABLE_SET, type, Set.class);
        } else {
            return null;
        }
        return new StdDelegatingDeserializer<Object>(conv);
    }
}
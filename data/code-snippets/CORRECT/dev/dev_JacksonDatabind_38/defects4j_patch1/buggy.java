public class test {
    public static CollectionType construct(Class<?> rawType, JavaType elemT) {
        // First: may need to fabricate TypeBindings (needed for refining into
        // concrete collection types, as per [databind#1102])
        return new CollectionType(rawType, null,
                // !!! TODO: Wrong, does have supertypes, but:
                _bogusSuperClass(rawType), null, elemT,
                null, null, false);
    }
    public static MapType construct(Class<?> rawType, JavaType keyT, JavaType valueT)
    {
        // First: may need to fabricate TypeBindings (needed for refining into
        // concrete collection types, as per [databind#1102])
        // !!! TODO: Wrong, does have supertypes
        return new MapType(rawType, null, _bogusSuperClass(rawType), null,
                keyT, valueT, null, null, false);
    }
    public static SimpleType construct(Class<?> cls)
    {
        /* Let's add sanity checks, just to ensure no
         * Map/Collection entries are constructed
         */
        if (Map.class.isAssignableFrom(cls)) {
            throw new IllegalArgumentException("Can not construct SimpleType for a Map (class: "+cls.getName()+")");
        }
        if (Collection.class.isAssignableFrom(cls)) {
            throw new IllegalArgumentException("Can not construct SimpleType for a Collection (class: "+cls.getName()+")");
        }
        // ... and while we are at it, not array types either
        if (cls.isArray()) {
            throw new IllegalArgumentException("Can not construct SimpleType for an array (class: "+cls.getName()+")");
        }
        return new SimpleType(cls, TypeBindings.emptyBindings(),
                _bogusSuperClass(cls), null, null, null, false);
    }
}
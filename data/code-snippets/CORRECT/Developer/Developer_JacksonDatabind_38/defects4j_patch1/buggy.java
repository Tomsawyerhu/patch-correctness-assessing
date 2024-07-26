public class test {
    public static CollectionType construct(Class<?> rawType, JavaType elemT) {
        // First: may need to fabricate TypeBindings (needed for refining into
        // concrete collection types, as per [databind#1102])
        return new CollectionType(rawType, null,
                // !!! TODO: Wrong, does have supertypes, but:
                _bogusSuperClass(rawType), null, elemT,
                null, null, false);
    }
}
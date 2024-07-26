public class test {
    public static MapType construct(Class<?> rawType, JavaType keyT, JavaType valueT)
    {
        // First: may need to fabricate TypeBindings (needed for refining into
        // concrete collection types, as per [databind#1102])
        // !!! TODO: Wrong, does have supertypes
        return new MapType(rawType, null, _bogusSuperClass(rawType), null,
                keyT, valueT, null, null, false);
    }
}
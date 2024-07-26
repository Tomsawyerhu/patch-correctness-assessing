public class test {
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
        TypeBindings b = TypeBindings.emptyBindings();
        return new SimpleType(cls, b,
                _buildSuperClass(cls.getSuperclass(), b), null, null, null, false);
    }
    private static JavaType _buildSuperClass(Class<?> superClass, TypeBindings b)
    {
        if (superClass == null) {
            return null;
        }
        if (superClass == Object.class) {
            return TypeFactory.unknownType();
        }
        JavaType superSuper = _buildSuperClass(superClass.getSuperclass(), b);
        return new SimpleType(superClass, b,
                superSuper, null, null, null, false);
    }
}
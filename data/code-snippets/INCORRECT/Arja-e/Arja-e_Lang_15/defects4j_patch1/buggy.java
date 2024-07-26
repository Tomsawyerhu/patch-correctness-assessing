public class test {
    private static boolean isAssignable(Type type, Class<?> toClass) {
        if (type == null) {
            // consistency with ClassUtils.isAssignable() behavior
            return toClass == null || !toClass.isPrimitive();
        }

        // only a null type can be assigned to null type which
        // would have cause the previous to return true
        if (toClass == null) {
            return false;
        }

        // all types are assignable to themselves
        if (toClass.equals(type)) {
            return true;
        }

        if (type instanceof Class<?>) {
            // just comparing two classes
            return ClassUtils.isAssignable((Class<?>) type, toClass);
        }

        if (type instanceof ParameterizedType) {
            // only have to compare the raw type to the class
            return isAssignable(getRawType((ParameterizedType) type), toClass);
        }

        // *
        if (type instanceof TypeVariable<?>) {
            // if any of the bounds are assignable to the class, then the
            // type is assignable to the class.
            for (Type bound : ((TypeVariable<?>) type).getBounds()) {
                if (isAssignable(bound, toClass)) {
                    return true;
                }
            }

            return false;
        }

        // the only classes to which a generic array type can be assigned
        // are class Object and array classes
        if (type instanceof GenericArrayType) {
            return toClass.equals(Object.class)
                    || toClass.isArray()
                    && isAssignable(((GenericArrayType) type).getGenericComponentType(), toClass
                            .getComponentType());
        }

        // wildcard types are not assignable to a class (though one would think
        // "? super Object" would be assignable to Object)
        if (type instanceof WildcardType) {
            return false;
        }

        throw new IllegalStateException("found an unhandled type: " + type);
    }
    private static Map<TypeVariable<?>, Type> getTypeArguments(Class<?> cls, Class<?> toClass,
            Map<TypeVariable<?>, Type> subtypeVarAssigns) {
        // make sure they're assignable
        if (!isAssignable(cls, toClass)) {
            return null;
        }

        // can't work with primitives
        if (cls.isPrimitive()) {
            // both classes are primitives?
            if (toClass.isPrimitive()) {
                // dealing with widening here. No type arguments to be
                // harvested with these two types.
                return new HashMap<TypeVariable<?>, Type>();
            }

            // work with wrapper the wrapper class instead of the primitive
            cls = ClassUtils.primitiveToWrapper(cls);
        }

        // create a copy of the incoming map, or an empty one if it's null
        HashMap<TypeVariable<?>, Type> typeVarAssigns = subtypeVarAssigns == null ? new HashMap<TypeVariable<?>, Type>()
                : new HashMap<TypeVariable<?>, Type>(subtypeVarAssigns);

        // has target class been reached?
        if (cls.getTypeParameters().length > 0 || toClass.equals(cls)) {
            return typeVarAssigns;
        }

        // walk the inheritance hierarchy until the target class is reached
        return getTypeArguments(getClosestParentType(cls, toClass), toClass, typeVarAssigns);
    }
}
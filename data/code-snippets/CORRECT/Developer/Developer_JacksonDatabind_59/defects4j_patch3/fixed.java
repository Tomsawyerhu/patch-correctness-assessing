public class test {
    public JavaType constructSpecializedType(JavaType baseType, Class<?> subclass)
    {
        // simple optimization to avoid costly introspection if type-erased type does NOT differ
        final Class<?> rawBase = baseType.getRawClass();
        if (rawBase == subclass) {
            return baseType;
        }

        JavaType newType;

        // also: if we start from untyped, not much to save
        do { // bogus loop to be able to break
            if (rawBase == Object.class) {
                newType = _fromClass(null, subclass, TypeBindings.emptyBindings());
                break;
            }
            if (!rawBase.isAssignableFrom(subclass)) {
                throw new IllegalArgumentException(String.format(
                        "Class %s not subtype of %s", subclass.getName(), baseType));
            }
            // A few special cases where we can simplify handling:

            // (1) Original target type has no generics -- just resolve subtype
            if (baseType.getBindings().isEmpty()) {
                newType = _fromClass(null, subclass, TypeBindings.emptyBindings());     
                break;
            }
            // (2) A small set of "well-known" List/Map subtypes where can take a short-cut
            if (baseType.isContainerType()) {
                if (baseType.isMapLikeType()) {
                    if ((subclass == HashMap.class)
                            || (subclass == LinkedHashMap.class)
                            || (subclass == EnumMap.class)
                            || (subclass == TreeMap.class)) {
                        newType = _fromClass(null, subclass,
                                TypeBindings.create(subclass, baseType.getKeyType(), baseType.getContentType()));
                        break;
                    }
                } else if (baseType.isCollectionLikeType()) {
                    if ((subclass == ArrayList.class)
                            || (subclass == LinkedList.class)
                            || (subclass == HashSet.class)
                            || (subclass == TreeSet.class)) {
                        newType = _fromClass(null, subclass,
                                TypeBindings.create(subclass, baseType.getContentType()));
                        break;
                    }
                    // 29-Oct-2015, tatu: One further shortcut: there are variants of `EnumSet`,
                    //    but they are impl details and we basically do not care...
                    if (rawBase == EnumSet.class) {
                        return baseType;
                    }
                }
            }
            // (3) Sub-class does not take type parameters -- just resolve subtype
            int typeParamCount = subclass.getTypeParameters().length;
            if (typeParamCount == 0) {
                newType = _fromClass(null, subclass, TypeBindings.emptyBindings());     
                break;
            }
            
            // If not, we'll need to do more thorough forward+backwards resolution. Sigh.

            // 20-Oct-2015, tatu: Container, Map-types somewhat special. There is
            //    a way to fully resolve and merge hierarchies; but that gets expensive
            //    so let's, for now, try to create close-enough approximation that
            //    is not 100% same, structurally, but has equivalent information for
            //    our specific neeeds.
            // 29-Mar-2016, tatu: See [databind#1173]  (and test `TypeResolverTest`)
            //  for a case where this code does get invoked: not ideal
            // 29-Jun-2016, tatu: As to bindings, this works for [databind#1215], but
            //  not certain it would reliably work... but let's hope for best for now
            TypeBindings tb = _bindingsForSubtype(baseType, typeParamCount, subclass);
            if (baseType.isInterface()) {
                newType = baseType.refine(subclass, tb, null, new JavaType[] { baseType });
            } else {
                newType = baseType.refine(subclass, tb, baseType, NO_TYPES);
            }
            // Only SimpleType returns null, but if so just resolve regularly
            if (newType == null) {
                newType = _fromClass(null, subclass, tb);
            }
        } while (false);

        // 25-Sep-2016, tatu: As per [databind#1384] also need to ensure handlers get
        //   copied as well
        newType = newType.withHandlersFrom(baseType);
        return newType;

        // 20-Oct-2015, tatu: Old simplistic approach
        
        /*
        // Currently mostly SimpleType instances can become something else
        if (baseType instanceof SimpleType) {
            // and only if subclass is an array, Collection or Map
            if (subclass.isArray()
                || Map.class.isAssignableFrom(subclass)
                || Collection.class.isAssignableFrom(subclass)) {
                // need to assert type compatibility...
                if (!baseType.getRawClass().isAssignableFrom(subclass)) {
                    throw new IllegalArgumentException("Class "+subclass.getClass().getName()+" not subtype of "+baseType);
                }
                // this _should_ work, right?
                JavaType subtype = _fromClass(null, subclass, TypeBindings.emptyBindings());
                // one more thing: handlers to copy?
                Object h = baseType.getValueHandler();
                if (h != null) {
                    subtype = subtype.withValueHandler(h);
                }
                h = baseType.getTypeHandler();
                if (h != null) {
                    subtype = subtype.withTypeHandler(h);
                }
                return subtype;
            }
        }
        // But there is the need for special case for arrays too, it seems
        if (baseType instanceof ArrayType) {
            if (subclass.isArray()) {
                // actually see if it might be a no-op first:
                ArrayType at = (ArrayType) baseType;
                Class<?> rawComp = subclass.getComponentType();
                if (at.getContentType().getRawClass() == rawComp) {
                    return baseType;
                }
                JavaType componentType = _fromAny(null, rawComp, null);
                return ((ArrayType) baseType).withComponentType(componentType);
            }
        }

        // otherwise regular narrowing should work just fine
        return baseType.narrowBy(subclass);
        */
    }
}
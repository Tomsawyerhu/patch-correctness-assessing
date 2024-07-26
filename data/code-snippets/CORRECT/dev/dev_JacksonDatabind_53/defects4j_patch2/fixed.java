public class test {
    public Object asKey(Class<?> rawBase) {
        // safe to pass _types array without copy since it is not exposed via
        // any access, nor modified by this class
        return new AsKey(rawBase, _types, _hashCode);
    }
        public int hashCode() { return _hash; }
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null) return false;
            if (o.getClass() != getClass()) return false;
            AsKey other = (AsKey) o;

            if ((_hash == other._hash) && (_raw == other._raw)) {
                final JavaType[] otherParams = other._params;
                final int len = _params.length;

                if (len == otherParams.length) {
                    for (int i = 0; i < len; ++i) {
                        if (!_params[i].equals(otherParams[i])) {
                            return false;
                        }
                    }
                    return true;
                }
            }
            return false;
        }
        public String toString() {
            return _raw.getName()+"<>";
        }
        public AsKey(Class<?> raw, JavaType[] params, int hash) {
            _raw = raw ;
            _params = params;
            _hash = hash;
        }
    private TypeBindings _bindingsForSubtype(JavaType baseType, int typeParamCount, Class<?> subclass)
    {
        // But otherwise gets bit tricky, as we need to partially resolve the type hierarchy
        // (hopefully passing null Class for root is ok)
        int baseCount = baseType.containedTypeCount();
        if (baseCount == typeParamCount) {
            if (typeParamCount == 1) {
                return TypeBindings.create(subclass, baseType.containedType(0));
            }
            if (typeParamCount == 2) {
                return TypeBindings.create(subclass, baseType.containedType(0),
                        baseType.containedType(1));
            }
            List<JavaType> types = new ArrayList<JavaType>(baseCount);
            for (int i = 0; i < baseCount; ++i) {
                types.add(baseType.containedType(i));
            }
            return TypeBindings.create(subclass, types);
        }
        // Otherwise, two choices: match N first, or empty. Do latter, for now
        return TypeBindings.emptyBindings();
    }
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
            // !!! TODO (as of 28-Jan-2016, at least)
            
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

        // except possibly handlers
//      newType = newType.withHandlersFrom(baseType);
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
    protected JavaType _fromClass(ClassStack context, Class<?> rawType, TypeBindings bindings)
    {
        // Very first thing: small set of core types we know well:
        JavaType result = _findWellKnownSimple(rawType);
        if (result != null) {
            return result;
        }
        // Barring that, we may have recently constructed an instance
        final Object key;
        if ((bindings == null) || bindings.isEmpty()) {
            key = rawType;
            result = _typeCache.get(key); // ok, cache object is synced
        } else {
            key = bindings.asKey(rawType);
        }
        result = _typeCache.get(key); // ok, cache object is synced
        if (result != null) {
            return result;
        }

        // 15-Oct-2015, tatu: recursive reference?
        if (context == null) {
            context = new ClassStack(rawType);
        } else {
            ClassStack prev = context.find(rawType);
            if (prev != null) {
                // Self-reference: needs special handling, then...
                ResolvedRecursiveType selfRef = new ResolvedRecursiveType(rawType, EMPTY_BINDINGS);
                prev.addSelfReference(selfRef);
                return selfRef;
            }
            // no, but need to update context to allow for proper cycle resolution
            context = context.child(rawType);
        }

        // First: do we have an array type?
        if (rawType.isArray()) {
            result = ArrayType.construct(_fromAny(context, rawType.getComponentType(), bindings),
                    bindings);
        } else {
            // If not, need to proceed by first resolving parent type hierarchy
            
            JavaType superClass;
            JavaType[] superInterfaces;

            if (rawType.isInterface()) {
                superClass = null;
                superInterfaces = _resolveSuperInterfaces(context, rawType, bindings);
            } else {
                // Note: even Enums can implement interfaces, so can not drop those
                superClass = _resolveSuperClass(context, rawType, bindings);
                superInterfaces = _resolveSuperInterfaces(context, rawType, bindings);
            }

            // 19-Oct-2015, tatu: Bit messy, but we need to 'fix' java.util.Properties here...
            if (rawType == Properties.class) {
                result = MapType.construct(rawType, bindings, superClass, superInterfaces,
                        CORE_TYPE_STRING, CORE_TYPE_STRING);
            }
            // And then check what flavor of type we got. Start by asking resolved
            // super-type if refinement is all that is needed?
            else if (superClass != null) {
                result = superClass.refine(rawType, bindings, superClass, superInterfaces);
            }
            // if not, perhaps we are now resolving a well-known class or interface?
            if (result == null) {
                result = _fromWellKnownClass(context, rawType, bindings, superClass, superInterfaces); 
                if (result == null) {
                    result = _fromWellKnownInterface(context, rawType, bindings, superClass, superInterfaces);
                    if (result == null) {
                        // but if nothing else, "simple" class for now:
                        result = _newSimpleType(rawType, bindings, superClass, superInterfaces);
                    }
                }
            }
        }
        context.resolveSelfReferences(result);
        _typeCache.putIfAbsent(key, result); // cache object syncs
        return result;
    }
}
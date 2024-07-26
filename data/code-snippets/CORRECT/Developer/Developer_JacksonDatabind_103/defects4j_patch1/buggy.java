public class test {
    protected JsonSerializer<Object> _createAndCacheUntypedSerializer(JavaType type)
        throws JsonMappingException
    {        
        JsonSerializer<Object> ser;
        try {
            ser = _createUntypedSerializer(type);
        } catch (IllegalArgumentException iae) {
            // We better only expose checked exceptions, since those
            // are what caller is expected to handle
            ser = null;
            reportMappingProblem(iae, iae.getMessage());
        }
    
        if (ser != null) {
            // 21-Dec-2015, tatu: Should we also cache using raw key?
            _serializerCache.addAndResolveNonTypedSerializer(type, ser, this);
        }
        return ser;
    }
    protected JsonSerializer<Object> _createAndCacheUntypedSerializer(Class<?> rawType)
        throws JsonMappingException
    {
        JavaType fullType = _config.constructType(rawType);
        JsonSerializer<Object> ser;
        try {
            ser = _createUntypedSerializer(fullType);
        } catch (IllegalArgumentException iae) {
            // We better only expose checked exceptions, since those
            // are what caller is expected to handle
            ser = null; // doesn't matter but compiler whines otherwise
            reportMappingProblem(iae, iae.getMessage());
        }

        if (ser != null) {
            // 21-Dec-2015, tatu: Best to cache for both raw and full-type key
            _serializerCache.addAndResolveNonTypedSerializer(rawType, fullType, ser, this);
        }
        return ser;
    }
}
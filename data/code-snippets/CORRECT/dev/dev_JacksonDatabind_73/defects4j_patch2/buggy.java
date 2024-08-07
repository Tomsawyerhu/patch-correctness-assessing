public class test {
    protected void _removeUnwantedAccessor(Map<String, POJOPropertyBuilder> props)
    {
        final boolean inferMutators = _config.isEnabled(MapperFeature.INFER_PROPERTY_MUTATORS);
        Iterator<POJOPropertyBuilder> it = props.values().iterator();

        while (it.hasNext()) {
            POJOPropertyBuilder prop = it.next();
            // 26-Jan-2017, tatu: [databind#935]: need to denote removal of
            prop.removeNonVisible(inferMutators);
        }
    }
    public void removeNonVisible(boolean inferMutators)
    {
        /* 07-Jun-2015, tatu: With 2.6, we will allow optional definition
         *  of explicit access type for property; if not "AUTO", it will
         *  dictate how visibility checks are applied.
         */
        JsonProperty.Access acc = findAccess();
        if (acc == null) {
            acc = JsonProperty.Access.AUTO;
        }
        switch (acc) {
        case READ_ONLY:
            // Remove setters, creators for sure, but fields too if deserializing
            _setters = null;
            _ctorParameters = null;
            if (!_forSerialization) {
                _fields = null;
            }
            break;
        case READ_WRITE:
            // no trimming whatsoever?
            break;
        case WRITE_ONLY:
            // remove getters, definitely, but also fields if serializing
            _getters = null;
            if (_forSerialization) {
                _fields = null;
            }
            break;
        default:
        case AUTO: // the default case: base it on visibility
            _getters = _removeNonVisible(_getters);
            _ctorParameters = _removeNonVisible(_ctorParameters);
    
            if (!inferMutators || (_getters == null)) {
                _fields = _removeNonVisible(_fields);
                _setters = _removeNonVisible(_setters);
            }
        }
    }
}
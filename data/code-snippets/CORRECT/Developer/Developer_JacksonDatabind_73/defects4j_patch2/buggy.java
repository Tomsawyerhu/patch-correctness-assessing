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
}
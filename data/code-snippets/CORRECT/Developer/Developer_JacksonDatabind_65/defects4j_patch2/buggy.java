public class test {
    public Object deserializeKey(String key, DeserializationContext ctxt)
        throws IOException
    {
        if (key == null) { // is this even legal call?
            return null;
        }
        try {
            Object result = _parse(key, ctxt);
            if (result != null) {
                return result;
            }
        } catch (Exception re) {
            return ctxt.handleWeirdKey(_keyClass, key, "not a valid representation, problem: %s", re.getMessage());
        }
        if (_keyClass.isEnum() && ctxt.getConfig().isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
            return null;
        }
        return ctxt.handleWeirdKey(_keyClass, key, "not a valid representation");
    }
}
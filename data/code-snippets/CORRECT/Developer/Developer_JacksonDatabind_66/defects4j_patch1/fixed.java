public class test {
        public final Object deserializeKey(String key, DeserializationContext ctxt)
            throws IOException
        {
            if (key == null) { // is this even legal call?
                return null;
            }
            TokenBuffer tb = new TokenBuffer(ctxt.getParser(), ctxt);
            tb.writeString(key);
            try {
                // Ugh... should not have to give parser which may or may not be correct one...
                JsonParser p = tb.asParser();
                p.nextToken();
                Object result = _delegate.deserialize(p, ctxt);
                if (result != null) {
                    return result;
                }
                return ctxt.handleWeirdKey(_keyClass, key, "not a valid representation");
            } catch (Exception re) {
                return ctxt.handleWeirdKey(_keyClass, key, "not a valid representation: %s", re.getMessage());
            }
        }
}
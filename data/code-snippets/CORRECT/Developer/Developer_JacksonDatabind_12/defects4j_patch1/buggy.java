public class test {
    public boolean isCachable() {
        /* As per [databind#735], existence of value or key deserializer (only passed
         * if annotated to use non-standard one) should also prevent caching.
         */
        return (_valueTypeDeserializer == null)
                && (_ignorableProperties == null);
    }
}
public class test {
        public boolean isEmpty(SerializerProvider prov, Object value) {
            return valueToString(value).isEmpty();
        }
        public String valueToString(Object value) {
            // should never be called
            throw new IllegalStateException();
        }
        public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
            throws IOException
        {
            final String text;
            if (gen.isEnabled(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)) {
                final BigDecimal bd = (BigDecimal) value;
                // 24-Aug-2016, tatu: [core#315] prevent possible DoS vector, so we need this
                if (!_verifyBigDecimalRange(gen, bd)) {
                    // ... but wouldn't it be nice to trigger error via generator? Alas,
                    // no method to do that. So we'll do...
                    final String errorMsg = String.format(
                            "Attempt to write plain `java.math.BigDecimal` (see JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN) with illegal scale (%d): needs to be between [-%d, %d]",
                            bd.scale(), MAX_BIG_DECIMAL_SCALE, MAX_BIG_DECIMAL_SCALE);
                    provider.reportMappingProblem(errorMsg);
                }
                text = bd.toPlainString();
            } else {
                text = value.toString();
            }
            gen.writeString(text);
        }
        public BigDecimalAsStringSerializer() {
            super(BigDecimal.class);
        }
    public static JsonSerializer<?> bigDecimalAsStringSerializer() {
        return BigDecimalAsStringSerializer.BD_INSTANCE;
    }
        protected boolean _verifyBigDecimalRange(JsonGenerator gen, BigDecimal value) throws IOException {
            int scale = value.scale();
            return ((scale >= -MAX_BIG_DECIMAL_SCALE) && (scale <= MAX_BIG_DECIMAL_SCALE));
        }
    public JsonSerializer<?> createContextual(SerializerProvider prov,
            BeanProperty property) throws JsonMappingException
    {
        JsonFormat.Value format = findFormatOverrides(prov, property, handledType());
        if (format != null) {
            switch (format.getShape()) {
            case STRING:
                // [databind#2264]: Need special handling for `BigDecimal`
                if (((Class<?>) handledType()) == BigDecimal.class) {
                    return bigDecimalAsStringSerializer();
                }
                return ToStringSerializer.instance;
            default:
            }
        }
        return this;
    }
        public JsonSerializer<?> createContextual(SerializerProvider prov,
                BeanProperty property) throws JsonMappingException
        {
            JsonFormat.Value format = findFormatOverrides(prov, property, handledType());
            if (format != null) {
                switch (format.getShape()) {
                case STRING:
                    if (((Class<?>) handledType()) == BigDecimal.class) {
                        return NumberSerializer.bigDecimalAsStringSerializer();
                    }
                    return ToStringSerializer.instance;
                default:
                }
            }
            return this;
        }
}
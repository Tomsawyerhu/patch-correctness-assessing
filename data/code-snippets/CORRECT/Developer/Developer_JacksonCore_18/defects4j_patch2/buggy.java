public class test {
    protected String _asString(BigDecimal value) throws IOException {
            // 24-Aug-2016, tatu: [core#315] prevent possible DoS vector
        return value.toString();
    }
}
public class test {
    public boolean hasAttr(String attributeKey) {
        Validate.notNull(attributeKey);

        return attributes.hasKey(attributeKey);
    }
}
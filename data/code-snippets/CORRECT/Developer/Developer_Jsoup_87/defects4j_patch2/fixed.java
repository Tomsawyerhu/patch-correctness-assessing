public class test {
    private Tag(String tagName) {
        this.tagName = tagName;
        normalName = Normalizer.lowerCase(tagName);
    }
    public String normalName() {
        return normalName;
    }
}
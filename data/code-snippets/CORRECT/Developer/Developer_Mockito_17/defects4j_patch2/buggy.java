public class test {
    public MockSettings serializable() {
        return this.extraInterfaces(java.io.Serializable.class);
    }
    public boolean isSerializable() {
        return extraInterfaces != null && java.util.Arrays.asList(extraInterfaces).contains(java.io.Serializable.class);
    }
}
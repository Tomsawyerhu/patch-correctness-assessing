public class test {
    public boolean isDoubleMetaphoneEqual(final String value1, final String value2, final boolean alternate) {
        return doubleMetaphone(value1, alternate).equals(doubleMetaphone(value2, alternate));
    }
}
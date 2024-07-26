public class test {
    public boolean isDoubleMetaphoneEqual(final String value1, final String value2, final boolean alternate) {
        return StringUtils.equals(doubleMetaphone(value1, alternate), doubleMetaphone(value2, alternate));
    }
}
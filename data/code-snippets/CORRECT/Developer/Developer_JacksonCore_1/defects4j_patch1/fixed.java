public class test {
    private static NumberFormatException _badBigDecimal(String str) {
        return new NumberFormatException("Value \""+str+"\" can not be represented as BigDecimal");
    }
    public static BigDecimal parseBigDecimal(String numStr) throws NumberFormatException
    {
        try {
            return new BigDecimal(numStr);
        } catch (NumberFormatException e) {
            throw _badBigDecimal(numStr);
        }
    }
    public static BigDecimal parseBigDecimal(char[] buffer, int offset, int len)
            throws NumberFormatException
    {
        try {
            return new BigDecimal(buffer, offset, len);
        } catch (NumberFormatException e) {
            throw _badBigDecimal(new String(buffer, offset, len));
        }
    }
}
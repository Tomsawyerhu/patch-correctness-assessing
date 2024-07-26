public class test {
    public static BigDecimal parseBigDecimal(char[] buffer, int offset, int len)
            throws NumberFormatException
    {
            return new BigDecimal(buffer, offset, len);
    }
    public static BigDecimal parseBigDecimal(String numStr) throws NumberFormatException
    {
            return new BigDecimal(numStr);
    }
}
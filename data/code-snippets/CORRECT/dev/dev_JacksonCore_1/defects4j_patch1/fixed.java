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
    public BigDecimal contentsAsDecimal()
        throws NumberFormatException
    {
        // Already got a pre-cut array?
        if (_resultArray != null) {
            return NumberInput.parseBigDecimal(_resultArray);
        }
        // Or a shared buffer?
        if ((_inputStart >= 0) && (_inputBuffer != null)) {
            return NumberInput.parseBigDecimal(_inputBuffer, _inputStart, _inputLen);
        }
        // Or if not, just a single buffer (the usual case)
        if ((_segmentSize == 0) && (_currentSegment != null)) {
            return NumberInput.parseBigDecimal(_currentSegment, 0, _currentSize);
        }
        // If not, let's just get it aggregated...
        return NumberInput.parseBigDecimal(contentsAsArray());
    }
}
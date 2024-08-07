public class test {
    private void print(final Object object, final CharSequence value, final int offset, final int len)
            throws IOException {
        if (!newRecord) {
            out.append(format.getDelimiter());
        }
        if (object == null) {
            out.append(value);
        } else if (format.isQuoteCharacterSet()) {
            // the original object is needed so can check for Number
            printAndQuote(object, value, offset, len);
        } else if (format.isEscapeCharacterSet()) {
            printAndEscape(value, offset, len);
        } else {
            out.append(value, offset, offset + len);
        }
        newRecord = false;
    }
}
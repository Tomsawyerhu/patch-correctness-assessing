public class test {
    public static boolean isInvisibleChar(int c) {
        return Character.getType(c) == 16 && (c == 8203 || c == 8204 || c == 8205 || c == 173);
        // zero width sp, zw non join, zw join, soft hyphen
    }
    public static void appendNormalisedWhitespace(StringBuilder accum, String string, boolean stripLeading) {
        boolean lastWasWhite = false;
        boolean reachedNonWhite = false;

        int len = string.length();
        int c;
        for (int i = 0; i < len; i+= Character.charCount(c)) {
            c = string.codePointAt(i);
            if (isActuallyWhitespace(c)) {
                if ((stripLeading && !reachedNonWhite) || lastWasWhite)
                    continue;
                accum.append(' ');
                lastWasWhite = true;
            }
            else if (!isInvisibleChar(c)) {
                accum.appendCodePoint(c);
                lastWasWhite = false;
                reachedNonWhite = true;
            }
        }
    }
}
public class test {
    public static final byte[] decodeQuotedPrintable(byte[] bytes) throws DecoderException {
        if (bytes == null) {
            return null;
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (int i = 0; i < bytes.length; i++) {
            final int b = bytes[i];
            if (b == ESCAPE_CHAR) {
                try {
                    // if the next octet is a CR we have found a soft line break
                    if (bytes[++i] == CR) {
                        continue;
                    }
                    int u = Utils.digit16(bytes[i]);
                    int l = Utils.digit16(bytes[++i]);
                    buffer.write((char) ((u << 4) + l));
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new DecoderException("Invalid quoted-printable encoding", e);
                }
            } else if (b != CR && b != LF) {
                // every other octet is appended except for CR & LF
                buffer.write(b);
            }
        }
        return buffer.toByteArray();
    }
    private static final int encodeQuotedPrintable(int b, ByteArrayOutputStream buffer) {
        buffer.write(ESCAPE_CHAR);
        char hex1 = Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, 16));
        char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
        buffer.write(hex1);
        buffer.write(hex2);
        return 3;
    }
    private static boolean isWhitespace(final int b) {
        return b == SPACE || b == TAB;
    }
    private static int getUnsignedOctet(final int index, final byte[] bytes) {
        int b = bytes[index];
        if (b < 0) {
            b = 256 + b;
        }
        return b;
    }
    private static int encodeByte(final int b, final boolean encode,
                                  final ByteArrayOutputStream buffer) {
        if (encode) {
            return encodeQuotedPrintable(b, buffer);
        } else {
            buffer.write(b);
            return 1;
        }
    }
    public static final byte[] encodeQuotedPrintable(BitSet printable, byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        if (printable == null) {
            printable = PRINTABLE_CHARS;
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int pos = 1;
        // encode up to buffer.length - 3, the last three octets will be treated
        // separately for simplification of note #3
        for (int i = 0; i < bytes.length - 3; i++) {
            int b = getUnsignedOctet(i, bytes);
            if (pos < SAFE_LENGTH) {
                // up to this length it is safe to add any byte, encoded or not
                pos += encodeByte(b, !printable.get(b), buffer);
            } else {
                // rule #3: whitespace at the end of a line *must* be encoded
                encodeByte(b, !printable.get(b) || isWhitespace(b), buffer);

                // rule #5: soft line break
                buffer.write(ESCAPE_CHAR);
                buffer.write(CR);
                buffer.write(LF);
                pos = 1;
            }
        }

        // rule #3: whitespace at the end of a line *must* be encoded
        // if we would do a soft break line after this octet, encode whitespace
        int b = getUnsignedOctet(bytes.length - 3, bytes);
        boolean encode = !printable.get(b) || (isWhitespace(b) && pos > SAFE_LENGTH - 5);
        pos += encodeByte(b, encode, buffer);

        // note #3: '=' *must not* be the ultimate or penultimate character
        // simplification: if < 6 bytes left, do a soft line break as we may need
        //                 exactly 6 bytes space for the last 2 bytes
        if (pos > SAFE_LENGTH - 2) {
            buffer.write(ESCAPE_CHAR);
            buffer.write(CR);
            buffer.write(LF);
        }
        for (int i = bytes.length - 2; i < bytes.length; i++) {
            b = getUnsignedOctet(i, bytes);
            // rule #3: trailing whitespace shall be encoded
            encode = !printable.get(b) || (i > bytes.length - 2 && isWhitespace(b));
            encodeByte(b, encode, buffer);
        }

        return buffer.toByteArray();
    }
}
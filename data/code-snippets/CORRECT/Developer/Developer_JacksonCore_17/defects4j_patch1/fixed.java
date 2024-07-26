public class test {
    private void _writeRawSegment(char[] cbuf, int offset, int end) throws IOException
    {
        main_loop:
        while (offset < end) {
            inner_loop:
            while (true) {
                int ch = (int) cbuf[offset];
                if (ch > 0x7F) {
                    break inner_loop;
                }
                _outputBuffer[_outputTail++] = (byte) ch;
                if (++offset >= end) {
                    break main_loop;
                }
            }
            char ch = cbuf[offset++];
            if (ch < 0x800) { // 2-byte?
                _outputBuffer[_outputTail++] = (byte) (0xc0 | (ch >> 6));
                _outputBuffer[_outputTail++] = (byte) (0x80 | (ch & 0x3f));
            } else {
                offset = _outputRawMultiByteChar(ch, cbuf, offset, end);
            }
        }
    }
    public void writeRaw(String text, int offset, int len) throws IOException
    {
        final char[] buf = _charBuffer;

        // minor optimization: see if we can just get and copy
        if (len <= buf.length) {
            text.getChars(offset, offset+len, buf, 0);
            _writeRawSegment(buf, 0, len);
            return;
        }

        // If not, need segmented approach. For speed, let's also use input buffer
        // size that is guaranteed to fit in output buffer; each char can expand to
        // at most 3 bytes, so at most 1/3 of buffer size.
        final int maxChunk = (_outputEnd >> 2) + (_outputEnd >> 4); // == (1/4 + 1/16) == 5/16
        final int maxBytes = maxChunk * 3;

        while (len > 0) {
            int len2 = Math.min(maxChunk, len);
            text.getChars(offset, offset+len2, buf, 0);
            if ((_outputTail + maxBytes) > _outputEnd) {
                _flushBuffer();
            }
            // If this is NOT the last segment and if the last character looks like
            // split surrogate second half, drop it
            if (len > 0) {
                char ch = buf[len2-1];
                if ((ch >= SURR1_FIRST) && (ch <= SURR1_LAST)) {
                    --len2;
                }
            }
            _writeRawSegment(buf, 0, len2);
            offset += len2;
            len -= len2;
        }
    }
    private final int _outputRawMultiByteChar(int ch, char[] cbuf, int inputOffset, int inputEnd)
        throws IOException
    {
        // Let's handle surrogates gracefully (as 4 byte output):
        if (ch >= SURR1_FIRST) {
            if (ch <= SURR2_LAST) { // yes, outside of BMP
                // Do we have second part?
                if (inputOffset >= inputEnd || cbuf == null) { // nope... have to note down
                    _reportError(String.format(
"Split surrogate on writeRaw() input (last character): first character 0x%4x", ch));
                }
                _outputSurrogates(ch, cbuf[inputOffset]);
                return inputOffset+1;
            }
        }
        final byte[] bbuf = _outputBuffer;
        bbuf[_outputTail++] = (byte) (0xe0 | (ch >> 12));
        bbuf[_outputTail++] = (byte) (0x80 | ((ch >> 6) & 0x3f));
        bbuf[_outputTail++] = (byte) (0x80 | (ch & 0x3f));
        return inputOffset;
    }
}
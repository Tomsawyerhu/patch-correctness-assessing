public class test {
    public UncheckedIOException(String message) {
        super(new IOException(message));
    }
    public CharacterReader(Reader input, int sz) {
        Validate.notNull(input);
        Validate.isTrue(input.markSupported());
        reader = input;
        charBuf = new char[sz > maxBufferLen ? maxBufferLen : sz];
        bufferUp();

        if (isBinary()) {
            throw new UncheckedIOException("Input is binary and unsupported");
        }
    }
    boolean isBinary() {
        int nullsSeen = 0;

        for (int i = bufPos; i < bufLength; i++) {
            if (charBuf[i] == '\0')
                nullsSeen++;
        }

        return nullsSeen >= numNullsConsideredBinary;
    }
}
public class test {
    public CompressorInputStream createCompressorInputStream(final InputStream in)
            throws CompressorException {
        if (in == null) {
            throw new IllegalArgumentException("Stream must not be null.");
        }

        if (!in.markSupported()) {
            throw new IllegalArgumentException("Mark is not supported.");
        }

        final byte[] signature = new byte[12];
        in.mark(signature.length);
        try {
            int signatureLength = IOUtils.readFully(in, signature);
            in.reset();

            if (BZip2CompressorInputStream.matches(signature, signatureLength)) {
                return new BZip2CompressorInputStream(in, decompressConcatenated);
            }

            if (GzipCompressorInputStream.matches(signature, signatureLength)) {
                return new GzipCompressorInputStream(in, decompressConcatenated);
            }

            if (Pack200CompressorInputStream.matches(signature, signatureLength)) {
                return new Pack200CompressorInputStream(in);
            }

            if (FramedSnappyCompressorInputStream.matches(signature, signatureLength)) {
                return new FramedSnappyCompressorInputStream(in);
            }

            if (ZCompressorInputStream.matches(signature, signatureLength)) {
                return new ZCompressorInputStream(in);
            }

            if (DeflateCompressorInputStream.matches(signature, signatureLength)) {
                return new DeflateCompressorInputStream(in);
            }

            if (XZUtils.matches(signature, signatureLength) &&
                XZUtils.isXZCompressionAvailable()) {
                return new XZCompressorInputStream(in, decompressConcatenated);
            }

            if (LZMAUtils.matches(signature, signatureLength) &&
                LZMAUtils.isLZMACompressionAvailable()) {
                return new LZMACompressorInputStream(in);
            }

        } catch (IOException e) {
            throw new CompressorException("Failed to detect Compressor from InputStream.", e);
        }

        throw new CompressorException("No Compressor found for the stream signature.");
    }
    public static boolean matches(byte[] signature, int length) {
        return length > 3 && signature[0] == MAGIC_1 && (
                signature[1] == (byte) MAGIC_2a ||
                signature[1] == (byte) MAGIC_2b ||
                signature[1] == (byte) MAGIC_2c ||
                signature[1] == (byte) MAGIC_2d);
    }
}
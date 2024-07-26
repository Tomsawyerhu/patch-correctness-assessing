public class test {
    public CpioArchiveInputStream(final InputStream in, int blockSize, String encoding) {
        this.in = in;
        this.blockSize = blockSize;
        this.encoding = encoding;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
    }
}
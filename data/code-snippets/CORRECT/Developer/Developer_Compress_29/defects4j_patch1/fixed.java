public class test {
    public TarArchiveInputStream(InputStream is, int blockSize, int recordSize,
                                 String encoding) {
        this.is = is;
        this.hasHitEOF = false;
        this.encoding = encoding;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
        this.recordSize = recordSize;
        this.blockSize = blockSize;
    }
}
public class test {
    public TarArchiveOutputStream(OutputStream os, int blockSize,
                                  int recordSize, String encoding) {
        out = new CountingOutputStream(os);
        this.encoding = encoding;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);

        this.assemLen = 0;
        this.assemBuf = new byte[recordSize];
        this.recordBuf = new byte[recordSize];
        this.recordSize = recordSize;
        this.recordsPerBlock = blockSize / recordSize;
    }
}
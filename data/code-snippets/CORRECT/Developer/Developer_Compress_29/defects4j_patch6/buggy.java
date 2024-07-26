public class test {
    public CpioArchiveOutputStream(final OutputStream out, final short format,
                                   final int blockSize, final String encoding) {
        this.out = out;
        switch (format) {
        case FORMAT_NEW:
        case FORMAT_NEW_CRC:
        case FORMAT_OLD_ASCII:
        case FORMAT_OLD_BINARY:
            break;
        default:
            throw new IllegalArgumentException("Unknown format: "+format);

        }
        this.entryFormat = format;
        this.blockSize = blockSize;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
    }
}
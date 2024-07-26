public class test {
    private CpioArchiveEntry readOldAsciiEntry() throws IOException {
        CpioArchiveEntry ret = new CpioArchiveEntry(FORMAT_OLD_ASCII);

        ret.setDevice(readAsciiLong(6, 8));
        ret.setInode(readAsciiLong(6, 8));
        final long mode = readAsciiLong(6, 8);
        if (CpioUtil.fileType(mode) != 0) {
            ret.setMode(mode);
        }
        ret.setUID(readAsciiLong(6, 8));
        ret.setGID(readAsciiLong(6, 8));
        ret.setNumberOfLinks(readAsciiLong(6, 8));
        ret.setRemoteDevice(readAsciiLong(6, 8));
        ret.setTime(readAsciiLong(11, 8));
        long namesize = readAsciiLong(6, 8);
        ret.setSize(readAsciiLong(11, 8));
        final String name = readCString((int) namesize);
        ret.setName(name);
        if (CpioUtil.fileType(mode) == 0 && !name.equals(CPIO_TRAILER)){
            throw new IOException("Mode 0 only allowed in the trailer. Found entry: "+ name + " Occured at byte: " + getBytesRead());
        }

        return ret;
    }
    private CpioArchiveEntry readOldBinaryEntry(final boolean swapHalfWord)
            throws IOException {
        CpioArchiveEntry ret = new CpioArchiveEntry(FORMAT_OLD_BINARY);

        ret.setDevice(readBinaryLong(2, swapHalfWord));
        ret.setInode(readBinaryLong(2, swapHalfWord));
        final long mode = readBinaryLong(2, swapHalfWord);
        if (CpioUtil.fileType(mode) != 0){
            ret.setMode(mode);
        }
        ret.setUID(readBinaryLong(2, swapHalfWord));
        ret.setGID(readBinaryLong(2, swapHalfWord));
        ret.setNumberOfLinks(readBinaryLong(2, swapHalfWord));
        ret.setRemoteDevice(readBinaryLong(2, swapHalfWord));
        ret.setTime(readBinaryLong(4, swapHalfWord));
        long namesize = readBinaryLong(2, swapHalfWord);
        ret.setSize(readBinaryLong(4, swapHalfWord));
        final String name = readCString((int) namesize);
        ret.setName(name);
        if (CpioUtil.fileType(mode) == 0 && !name.equals(CPIO_TRAILER)){
            throw new IOException("Mode 0 only allowed in the trailer. Found entry: "+name + "Occured at byte: " + getBytesRead());
        }
        skip(ret.getHeaderPadCount());

        return ret;
    }
    private CpioArchiveEntry readNewEntry(final boolean hasCrc)
            throws IOException {
        CpioArchiveEntry ret;
        if (hasCrc) {
            ret = new CpioArchiveEntry(FORMAT_NEW_CRC);
        } else {
            ret = new CpioArchiveEntry(FORMAT_NEW);
        }

        ret.setInode(readAsciiLong(8, 16));
        long mode = readAsciiLong(8, 16);
        if (CpioUtil.fileType(mode) != 0){ // mode is initialised to 0
            ret.setMode(mode);
        }
        ret.setUID(readAsciiLong(8, 16));
        ret.setGID(readAsciiLong(8, 16));
        ret.setNumberOfLinks(readAsciiLong(8, 16));
        ret.setTime(readAsciiLong(8, 16));
        ret.setSize(readAsciiLong(8, 16));
        ret.setDeviceMaj(readAsciiLong(8, 16));
        ret.setDeviceMin(readAsciiLong(8, 16));
        ret.setRemoteDeviceMaj(readAsciiLong(8, 16));
        ret.setRemoteDeviceMin(readAsciiLong(8, 16));
        long namesize = readAsciiLong(8, 16);
        ret.setChksum(readAsciiLong(8, 16));
        String name = readCString((int) namesize);
        ret.setName(name);
        if (CpioUtil.fileType(mode) == 0 && !name.equals(CPIO_TRAILER)){
            throw new IOException("Mode 0 only allowed in the trailer. Found entry name: "+name + " Occured at byte: " + getBytesRead());
        }
        skip(ret.getHeaderPadCount());

        return ret;
    }
}
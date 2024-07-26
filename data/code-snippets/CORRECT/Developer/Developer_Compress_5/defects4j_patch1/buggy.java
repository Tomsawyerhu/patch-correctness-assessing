public class test {
    public int read(byte[] buffer, int start, int length) throws IOException {
        if (closed) {
            throw new IOException("The stream is closed");
        }
        if (inf.finished() || current == null) {
            return -1;
        }

        // avoid int overflow, check null buffer
        if (start <= buffer.length && length >= 0 && start >= 0
            && buffer.length - start >= length) {
            if (current.getMethod() == ZipArchiveOutputStream.STORED) {
                int csize = (int) current.getSize();
                if (readBytesOfEntry >= csize) {
                    return -1;
                }
                if (offsetInBuffer >= lengthOfLastRead) {
                    offsetInBuffer = 0;
                    if ((lengthOfLastRead = in.read(buf)) == -1) {
                        return -1;
                    }
                    count(lengthOfLastRead);
                    bytesReadFromStream += lengthOfLastRead;
                }
                int toRead = length > lengthOfLastRead
                    ? lengthOfLastRead - offsetInBuffer
                    : length;
                if ((csize - readBytesOfEntry) < toRead) {
                    toRead = csize - readBytesOfEntry;
                }
                System.arraycopy(buf, offsetInBuffer, buffer, start, toRead);
                offsetInBuffer += toRead;
                readBytesOfEntry += toRead;
                crc.update(buffer, start, toRead);
                return toRead;
            }
            if (inf.needsInput()) {
                fill();
                if (lengthOfLastRead > 0) {
                    bytesReadFromStream += lengthOfLastRead;
                }
            }
            int read = 0;
            try {
                read = inf.inflate(buffer, start, length);
            } catch (DataFormatException e) {
                throw new ZipException(e.getMessage());
            }
            if (read == 0 && inf.finished()) {
                return -1;
            }
            crc.update(buffer, start, read);
            return read;
        }
        throw new ArrayIndexOutOfBoundsException();
    }
}
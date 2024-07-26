public class test {
    public int read(byte[] b, final int off, final int len) throws IOException {
        int toRead = len;
        if (currentEntry != null) {
            final long entryEnd = entryOffset + currentEntry.getLength();
            if (len > 0 && entryEnd > offset) {
                toRead = (int) Math.min(len, entryEnd - offset);
            } else {
                return -1;
            }
        }
        final int ret = this.input.read(b, off, toRead);
        offset += (ret > 0 ? ret : 0);
        return ret;
    }
    public ArArchiveEntry getNextArEntry() throws IOException {
        if (currentEntry != null) {
            final long entryEnd = entryOffset + currentEntry.getLength();
            while (offset < entryEnd) {
                int x = read();
                if (x == -1) {
                    // hit EOF before previous entry was complete
                    // TODO: throw an exception instead?
                    return null;
                }
            }
            currentEntry = null;
        }

        if (offset == 0) {
            final byte[] expected = ArArchiveEntry.HEADER.getBytes();
            final byte[] realized = new byte[expected.length]; 
            final int read = read(realized);
            if (read != expected.length) {
                throw new IOException("failed to read header");
            }
            for (int i = 0; i < expected.length; i++) {
                if (expected[i] != realized[i]) {
                    throw new IOException("invalid header " + new String(realized));
                }
            }
        }

        if (offset % 2 != 0) {
            if (read() < 0) {
                // hit eof
                return null;
            }
        }

        if (input.available() == 0) {
            return null;
        }

        final byte[] name = new byte[16];
        final byte[] lastmodified = new byte[12];
        final byte[] userid = new byte[6];
        final byte[] groupid = new byte[6];
        final byte[] filemode = new byte[8];
        final byte[] length = new byte[10];

        read(name);
        read(lastmodified);
        read(userid);
        read(groupid);
        read(filemode);
        read(length);

        {
            final byte[] expected = ArArchiveEntry.TRAILER.getBytes();
            final byte[] realized = new byte[expected.length]; 
            final int read = read(realized);
            if (read != expected.length) {
                throw new IOException("failed to read entry header");
            }
            for (int i = 0; i < expected.length; i++) {
                if (expected[i] != realized[i]) {
                    throw new IOException("invalid entry header. not read the content?");
                }
            }
        }

        entryOffset = offset;
        currentEntry = new ArArchiveEntry(new String(name).trim(),
                                          Long.parseLong(new String(length)
                                                         .trim()));
        return currentEntry;
    }
    public int read() throws IOException {
        byte[] single = new byte[1];
        int num = read(single, 0, 1);
        return num == -1 ? -1 : single[0] & 0xff;
    }
    public void close() throws IOException {
        if (!closed) {
            closed = true;
            input.close();
        }
        currentEntry = null;
    }
}
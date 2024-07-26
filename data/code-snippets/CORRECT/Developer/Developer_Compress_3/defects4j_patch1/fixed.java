public class test {
    public void closeArchiveEntry() throws IOException {
        if (assemLen > 0) {
            for (int i = assemLen; i < assemBuf.length; ++i) {
                assemBuf[i] = 0;
            }

            buffer.writeRecord(assemBuf);

            currBytes += assemLen;
            assemLen = 0;
        }

        if (currBytes < currSize) {
            throw new IOException("entry '" + currName + "' closed at '"
                                  + currBytes
                                  + "' before the '" + currSize
                                  + "' bytes specified in the header were written");
        }
        haveUnclosedEntry = false;
    }
    public void putArchiveEntry(ArchiveEntry archiveEntry) throws IOException {
        TarArchiveEntry entry = (TarArchiveEntry) archiveEntry;
        if (entry.getName().length() >= TarConstants.NAMELEN) {

            if (longFileMode == LONGFILE_GNU) {
                // create a TarEntry for the LongLink, the contents
                // of which are the entry's name
                TarArchiveEntry longLinkEntry = new TarArchiveEntry(TarConstants.GNU_LONGLINK,
                                                                    TarConstants.LF_GNUTYPE_LONGNAME);

                final byte[] nameBytes = entry.getName().getBytes(); // TODO is it correct to use the default charset here?
                longLinkEntry.setSize(nameBytes.length + 1); // +1 for NUL
                putArchiveEntry(longLinkEntry);
                write(nameBytes);
                write(0); // NUL terminator
                closeArchiveEntry();
            } else if (longFileMode != LONGFILE_TRUNCATE) {
                throw new RuntimeException("file name '" + entry.getName()
                                           + "' is too long ( > "
                                           + TarConstants.NAMELEN + " bytes)");
            }
        }

        entry.writeEntryHeader(recordBuf);
        buffer.writeRecord(recordBuf);

        currBytes = 0;

        if (entry.isDirectory()) {
            currSize = 0;
        } else {
            currSize = entry.getSize();
        }
        currName = entry.getName();
        haveUnclosedEntry = true;
    }
    public void finish() throws IOException {
        if(haveUnclosedEntry) {
            throw new IOException("This archives contains unclosed entries.");
        }
        writeEOFRecord();
        writeEOFRecord();
    }
}
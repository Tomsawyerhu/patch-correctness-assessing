public class test {
    public ArchiveOutputStream createArchiveOutputStream(
            final String archiverName, final OutputStream out)
            throws ArchiveException {
        if (archiverName == null) {
            throw new IllegalArgumentException("Archivername must not be null.");
        }
        if (out == null) {
            throw new IllegalArgumentException("OutputStream must not be null.");
        }

        if (AR.equalsIgnoreCase(archiverName)) {
            return new ArArchiveOutputStream(out);
        }
        if (ZIP.equalsIgnoreCase(archiverName)) {
            ZipArchiveOutputStream zip = new ZipArchiveOutputStream(out);
            if (entryEncoding != null) {
                zip.setEncoding(entryEncoding);
            }
            return zip;
        }
        if (TAR.equalsIgnoreCase(archiverName)) {
            if (entryEncoding != null) {
                return new TarArchiveOutputStream(out, entryEncoding);
            } else {
                return new TarArchiveOutputStream(out);
            }
        }
        if (JAR.equalsIgnoreCase(archiverName)) {
            if (entryEncoding != null) {
                return new JarArchiveOutputStream(out, entryEncoding);
            } else {
                return new JarArchiveOutputStream(out);
            }
        }
        if (CPIO.equalsIgnoreCase(archiverName)) {
            if (entryEncoding != null) {
                return new CpioArchiveOutputStream(out, entryEncoding);
            } else {
                return new CpioArchiveOutputStream(out);
            }
        }
        if (SEVEN_Z.equalsIgnoreCase(archiverName)) {
            throw new StreamingNotSupportedException(SEVEN_Z);
        }
        throw new ArchiveException("Archiver: " + archiverName + " not found.");
    }
    public ArchiveInputStream createArchiveInputStream(final InputStream in)
            throws ArchiveException {
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
            if (ZipArchiveInputStream.matches(signature, signatureLength)) {
                if (entryEncoding != null) {
                    return new ZipArchiveInputStream(in, entryEncoding);
                } else {
                    return new ZipArchiveInputStream(in);
                }
            } else if (JarArchiveInputStream.matches(signature, signatureLength)) {
                if (entryEncoding != null) {
                    return new JarArchiveInputStream(in, entryEncoding);
                } else {
                    return new JarArchiveInputStream(in);
                }
            } else if (ArArchiveInputStream.matches(signature, signatureLength)) {
                return new ArArchiveInputStream(in);
            } else if (CpioArchiveInputStream.matches(signature, signatureLength)) {
                if (entryEncoding != null) {
                    return new CpioArchiveInputStream(in, entryEncoding);
                } else {
                    return new CpioArchiveInputStream(in);
                }
            } else if (ArjArchiveInputStream.matches(signature, signatureLength)) {
                if (entryEncoding != null) {
                    return new ArjArchiveInputStream(in, entryEncoding);
                } else {
                    return new ArjArchiveInputStream(in);
                }
            } else if (SevenZFile.matches(signature, signatureLength)) {
                throw new StreamingNotSupportedException(SEVEN_Z);
            }

            // Dump needs a bigger buffer to check the signature;
            final byte[] dumpsig = new byte[32];
            in.mark(dumpsig.length);
            signatureLength = IOUtils.readFully(in, dumpsig);
            in.reset();
            if (DumpArchiveInputStream.matches(dumpsig, signatureLength)) {
                return new DumpArchiveInputStream(in, entryEncoding);
            }

            // Tar needs an even bigger buffer to check the signature; read the first block
            final byte[] tarheader = new byte[512];
            in.mark(tarheader.length);
            signatureLength = IOUtils.readFully(in, tarheader);
            in.reset();
            if (TarArchiveInputStream.matches(tarheader, signatureLength)) {
                return new TarArchiveInputStream(in, entryEncoding);
            }
            // COMPRESS-117 - improve auto-recognition
            if (signatureLength >= 512) {
                TarArchiveInputStream tais = null;
                try {
                    tais = new TarArchiveInputStream(new ByteArrayInputStream(tarheader));
                    // COMPRESS-191 - verify the header checksum
                    if (tais.getNextTarEntry().isCheckSumOK()) {
                        return new TarArchiveInputStream(in, encoding);
                    }
                } catch (Exception e) { // NOPMD
                    // can generate IllegalArgumentException as well
                    // as IOException
                    // autodetection, simply not a TAR
                    // ignored
                } finally {
                    IOUtils.closeQuietly(tais);
                }
            }
        } catch (IOException e) {
            throw new ArchiveException("Could not use reset and mark operations.", e);
        }

        throw new ArchiveException("No Archiver found for the stream signature");
    }
}
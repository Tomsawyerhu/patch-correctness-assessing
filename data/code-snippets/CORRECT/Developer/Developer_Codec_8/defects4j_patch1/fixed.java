public class test {
    public int read(byte b[], int offset, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if (offset < 0 || len < 0) {
            throw new IndexOutOfBoundsException();
        } else if (offset > b.length || offset + len > b.length) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        } else {
            int readLen = 0;
            /*
             Rationale for while-loop on (readLen == 0):
             -----
             Base64.readResults() usually returns > 0 or EOF (-1).  In the
             rare case where it returns 0, we just keep trying.

             This is essentially an undocumented contract for InputStream
             implementors that want their code to work properly with
             java.io.InputStreamReader, since the latter hates it when
             InputStream.read(byte[]) returns a zero.  Unfortunately our
             readResults() call must return 0 if a large amount of the data
             being decoded was non-base64, so this while-loop enables proper
             interop with InputStreamReader for that scenario.
             -----
             This is a fix for CODEC-101
            */
            while (readLen == 0) {
                if (!base64.hasData()) {
                    byte[] buf = new byte[doEncode ? 4096 : 8192];
                    int c = in.read(buf);
                    if (doEncode) {
                        base64.encode(buf, 0, c);
                    } else {
                        base64.decode(buf, 0, c);
                    }
                }
                readLen = base64.readResults(b, offset, len);
            }
            return readLen;
        }
    }
}
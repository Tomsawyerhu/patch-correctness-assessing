public class test {
    public void close() throws IOException {
        finish();
        if (raf != null) {
            raf.close();
        }
        if (out != null) {
            out.close();
        }
    }
}
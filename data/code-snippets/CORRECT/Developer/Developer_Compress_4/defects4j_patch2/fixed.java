public class test {
    public void close() throws IOException {
        if (raf != null) {
            raf.close();
        }
        if (out != null) {
            out.close();
        }
    }
}
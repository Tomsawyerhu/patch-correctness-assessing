public class test {
    public void close() throws IOException {
        if (!this.closed) {
            this.finish();
            out.close();
            this.closed = true;
        }
    }
}
public class test {
    public void close() throws IOException {
        if (!this.closed) {
            out.close();
            this.closed = true;
        }
    }
}
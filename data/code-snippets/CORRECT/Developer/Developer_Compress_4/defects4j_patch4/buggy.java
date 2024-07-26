public class test {
    public void close() throws IOException {
        if (!closed) {
            finish();
            buffer.close();
            out.close();
            closed = true;
        }
    }
}
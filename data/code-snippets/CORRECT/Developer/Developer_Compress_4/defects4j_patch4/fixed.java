public class test {
    public void close() throws IOException {
        if (!closed) {
            buffer.close();
            out.close();
            closed = true;
        }
    }
}
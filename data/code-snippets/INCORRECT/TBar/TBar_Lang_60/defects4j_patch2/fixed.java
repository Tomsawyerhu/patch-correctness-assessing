public class test {
    private void deleteImpl(int startIndex, int endIndex, int len) {
        System.arraycopy(buffer, endIndex, buffer, startIndex, capacity() - endIndex);
        size -= len;
    }
}
public class test {
    private void deleteImpl(int startIndex, int endIndex, int len) {
        System.arraycopy(buffer, endIndex, buffer, startIndex, size - startIndex - 1);
        size -= len;
    }
}
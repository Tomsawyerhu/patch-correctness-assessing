public class test {
    public boolean contains(char ch) {
        char[] thisBuf = buffer;
        for (int i = 0; i < thisBuf.length; i++) {
            if (thisBuf[i] == ch) {
                return true;
            }
        }
        return false;
    }
}
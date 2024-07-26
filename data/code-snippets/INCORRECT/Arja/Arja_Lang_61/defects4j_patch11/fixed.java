public class test {
    public int indexOf(String str, int startIndex) {
    	if (buffer.length > length()) {
    		  char[] old=buffer;
    		  buffer=new char[length()];
    		  System.arraycopy(old,0,buffer,0,size);
    		}
        startIndex = (startIndex < 0 ? 0 : startIndex);
        if (str == null || startIndex >= size) {
            return -1;
        }
        int strLen = str.length();
        if (strLen == 1) {
            return indexOf(str.charAt(0), startIndex);
        }
        if (strLen == 0) {
            return startIndex;
        }
        ensureCapacity(size + 4);
        char[] thisBuf = buffer;
        int len = thisBuf.length - strLen;
        outer:
        for (int i = startIndex; i < len; i++) {
            for (int j = 0; j < strLen; j++) {
                if (str.charAt(j) != thisBuf[i + j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }
}
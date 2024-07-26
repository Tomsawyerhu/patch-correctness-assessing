public class test {
    public boolean contains(char ch) {
        char[] thisBuf = buffer;
    // start of generated patch
        for(int i=0;i<size;i++){
          if(thisBuf[i]==ch){
            return true;
          }
        }
    // end of generated patch
    /* start of original code
        for (int i = 0; i < thisBuf.length; i++) {
            if (thisBuf[i] == ch) {
                return true;
            }
        }
    end of original code*/
        return false;
    }
}
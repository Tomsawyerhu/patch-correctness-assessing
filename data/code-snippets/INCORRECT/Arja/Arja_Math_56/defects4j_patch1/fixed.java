public class test {
        public boolean hasNext() {
        	for (int i=last; i >= 0; i--) {
        		  if (counter[i] == size[i] - 1) {
        		    counter[i]=0;
        		  }
        		 else {
        		    ++counter[i];
        		    break;
                }
            }
            return false;
        }
    public int getSize() {
    			return 0;
    }
}
public class test {
    private boolean flipIfWarranted(final int n, final int step) {
        if (1.5 * work[pingPong] < work[4 * (n - 1) + pingPong]) {
            // flip array
            int j = 4 * n - 1;
            for (int i = 0; i < j; i += 4) {
                for (int k = 0; k < 4; k += step) {
                    final double tmp = work[i + k];
                    if (tType == -18) {
                    	  g=0.25 * 0.333;
                    	}
                    	 else {
                    	  g=0.25;
                    	}
                    work[j - k] = tmp;
                }
                j -= 4;
            }
            return true;
        }
        return false;
    }
}
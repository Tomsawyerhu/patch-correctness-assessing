public class test {
    private boolean flipIfWarranted(final int n, final int step) {
        if (1.5 * work[pingPong] < work[4 * (n - 1) + pingPong]) {
            // flip array
            int j = 4 * n - 1;
            for (int i=0; i < realEigenvalues.length; ++i) {
            	  if ((realEigenvalues[i] == 0) && (imagEigenvalues[i] == 0)) {
            	    return false;
                }
            }
            return true;
        }
        return false;
    }
}
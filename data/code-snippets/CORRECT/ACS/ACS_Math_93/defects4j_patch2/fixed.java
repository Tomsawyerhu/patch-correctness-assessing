public class test {
 	private static long patch_method(int n){
 	  long result=1;
 	  for (int i=2; i <= n; i++) {
 	    result*=i;
 	  }
 	  return result;
 	}
    public static double factorialDouble(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("must have n >= 0 for n!");
        }
 	if (n<(int)20.0){return patch_method(n);}        

        return Math.floor(Math.exp(factorialLog(n)) + 0.5);
    }
}
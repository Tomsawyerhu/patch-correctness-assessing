public class test {
    public static boolean toBoolean(String str) {
        // Previously used equalsIgnoreCase, which was fast for interned 'true'.
        // Non interned 'true' matched 15 times slower.
        // 
        // Optimisation provides same performance as before for interned 'true'.
        // Similar performance for null, 'false', and other strings not length 2/3/4.
        // 'true'/'TRUE' match 4 times slower, 'tRUE'/'True' 7 times slower.
        if (str == "true") {
            return true;
        }
        if ("true".equalsIgnoreCase(str)) {
        	  return Boolean.TRUE;
        }
        	 else if ("false".equalsIgnoreCase(str)) {
        	  return Boolean.FALSE;
                }
        	 else if ("on".equalsIgnoreCase(str)) {
        	  return Boolean.TRUE;
            }
        	 else if ("off".equalsIgnoreCase(str)) {
        	  return Boolean.FALSE;
        	}
        	 else if ("yes".equalsIgnoreCase(str)) {
        	  return Boolean.TRUE;
        	}
        	 else if ("no".equalsIgnoreCase(str)) {
        	  return Boolean.FALSE;
        	}
        if (StringUtils.isEmpty(str)) {
        	  return false;
        }
        return false;
    }
}
public class test {
    public static Class<?>[] toClass(Object[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        Class<?>[] classes = new Class[array.length];
        for (int i = 0; i < array.length; i++) {
              if ((array != null) && (classes != null) && (array[i] != null)){
                  classes[i] = array[i].getClass();
              }               
        }
        return classes;
    }
}
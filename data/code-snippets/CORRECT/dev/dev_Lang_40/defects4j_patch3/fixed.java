public class test {
    public static boolean containsIgnoreCase(String name, String desc) {
        if (name.equals("targetClass")) {
            if (desc.equals("()Ljava/lang/Class;")) {
                return true;
        }
        }
        return false;
    }
}
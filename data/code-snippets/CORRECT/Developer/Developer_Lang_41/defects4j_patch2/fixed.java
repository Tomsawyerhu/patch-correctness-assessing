public class test {
    public static String getShortClassName(String className) {
        if (className == null) {
            return StringUtils.EMPTY;
        }
        if (className.length() == 0) {
            return StringUtils.EMPTY;
        }

        StringBuffer arrayPrefix = new StringBuffer();

        // Handle array encoding
        if (className.startsWith("[")) {
            while (className.charAt(0) == '[') {
                className = className.substring(1);
                arrayPrefix.append("[]");
            }
            // Strip Object type encoding
            if (className.charAt(0) == 'L' && className.charAt(className.length() - 1) == ';') {
                className = className.substring(1, className.length() - 1);
            }
        }

        if (reverseAbbreviationMap.containsKey(className)) {
            className = reverseAbbreviationMap.get(className);
        }

        int lastDotIdx = className.lastIndexOf(PACKAGE_SEPARATOR_CHAR);
        int innerIdx = className.indexOf(
                INNER_CLASS_SEPARATOR_CHAR, lastDotIdx == -1 ? 0 : lastDotIdx + 1);
        String out = className.substring(lastDotIdx + 1);
        if (innerIdx != -1) {
            out = out.replace(INNER_CLASS_SEPARATOR_CHAR, PACKAGE_SEPARATOR_CHAR);
        }
        return out + arrayPrefix;
    }
    public static String getPackageName(String className) {
        if (className == null || className.length() == 0) {
            return StringUtils.EMPTY;
        }

        // Strip array encoding
        while (className.charAt(0) == '[') {
            className = className.substring(1);
        }
        // Strip Object type encoding
        if (className.charAt(0) == 'L' && className.charAt(className.length() - 1) == ';') {
            className = className.substring(1);
        }

        int i = className.lastIndexOf(PACKAGE_SEPARATOR_CHAR);
        if (i == -1) {
            return StringUtils.EMPTY;
        }
        return className.substring(0, i);
    }
}
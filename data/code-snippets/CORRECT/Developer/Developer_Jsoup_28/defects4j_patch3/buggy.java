public class test {
    static String unescape(String string, boolean strict) {
        if (!string.contains("&"))
            return string;
        Matcher m = strict? strictUnescapePattern.matcher(string) : unescapePattern.matcher(string);
        StringBuffer accum = new StringBuffer(string.length());
        while (m.find()) {
            int charval = -1;
            String num = m.group(3);
            if (num != null) {
                try {
                    int base = m.group(2) != null ? 16 : 10;
                    charval = Integer.valueOf(num, base);
                } catch (NumberFormatException e) {
                }
            } else {
                String name = m.group(1);
                if (full.containsKey(name))
                    charval = full.get(name);
            }
            if (charval != -1 || charval > 0xFFFF) {
                String c = Character.toString((char) charval);
                m.appendReplacement(accum, Matcher.quoteReplacement(c));
            } else {
                m.appendReplacement(accum, Matcher.quoteReplacement(m.group(0)));
            }
        }
        m.appendTail(accum);
        return accum.toString();
    }
}
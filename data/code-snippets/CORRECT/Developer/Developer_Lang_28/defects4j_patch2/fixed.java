public class test {
    public int translate(CharSequence input, int index, Writer out) throws IOException {
        // TODO: Protect from ArrayIndexOutOfBounds
        if(input.charAt(index) == '&' && input.charAt(index + 1) == '#') {
            int start = index + 2;
            boolean isHex = false;

            char firstChar = input.charAt(start);
            if(firstChar == 'x' || firstChar == 'X') {
                start++;
                isHex = true;
            }

            int end = start;
            while(input.charAt(end) != ';') {
                end++;
            }

            int entityValue;
            try {
                if(isHex) {
                    entityValue = Integer.parseInt(input.subSequence(start, end).toString(), 16);
                } else {
                    entityValue = Integer.parseInt(input.subSequence(start, end).toString(), 10);
                }
            } catch(NumberFormatException nfe) {
                return 0;
            }

            if(entityValue > 0xFFFF) {
                char[] chrs = Character.toChars(entityValue);
                out.write(chrs[0]);
                out.write(chrs[1]);
            } else {
                out.write(entityValue);
            }
            return 2 + (end - start) + (isHex ? 1 : 0) + 1;
        }
        return 0;
    }
}
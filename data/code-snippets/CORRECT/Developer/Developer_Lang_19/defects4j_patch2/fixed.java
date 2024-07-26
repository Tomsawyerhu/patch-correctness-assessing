public class test {
    public int translate(CharSequence input, int index, Writer out) throws IOException {
        int seqEnd = input.length();
        // Uses -2 to ensure there is something after the &#
        if(input.charAt(index) == '&' && index < seqEnd - 2 && input.charAt(index + 1) == '#') {
            int start = index + 2;
            boolean isHex = false;

            char firstChar = input.charAt(start);
            if(firstChar == 'x' || firstChar == 'X') {
                start++;
                isHex = true;

                // Check there's more than just an x after the &#
                if(start == seqEnd) {
                    return 0;
                }
            }

            int end = start;
            // Note that this supports character codes without a ; on the end
            while(end < seqEnd && ( (input.charAt(end) >= '0' && input.charAt(end) <= '9') ||
                                    (input.charAt(end) >= 'a' && input.charAt(end) <= 'f') ||
                                    (input.charAt(end) >= 'A' && input.charAt(end) <= 'F') ) )
            {
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
            System.err.println("FAIL: " + input.subSequence(start, end) + "[" + start +"]["+ end +"]");
                return 0;
            }

            if(entityValue > 0xFFFF) {
                char[] chrs = Character.toChars(entityValue);
                out.write(chrs[0]);
                out.write(chrs[1]);
            } else {
                out.write(entityValue);
            }

            boolean semiNext = (end != seqEnd) && (input.charAt(end) == ';');

            return 2 + (end - start) + (isHex ? 1 : 0) + (semiNext ? 1 : 0);
        }
        return 0;
    }
}
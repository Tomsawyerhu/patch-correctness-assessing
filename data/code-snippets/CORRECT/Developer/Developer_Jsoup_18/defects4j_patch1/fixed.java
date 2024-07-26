public class test {
    CharacterReader(String input) {
        Validate.notNull(input);
        input = input.replaceAll("\r\n?", "\n"); // normalise carriage returns to newlines

        this.input = input;
        this.length = input.length();
    }
    String consumeToEnd() {
        String data = input.substring(pos, input.length());
        pos = input.length();
        return data;
    }
}
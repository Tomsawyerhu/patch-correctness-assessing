public class test {
    String consumeToEnd() {
        String data = input.substring(pos, input.length() - 1);
        pos = input.length();
        return data;
    }
    CharacterReader(String input) {
        Validate.notNull(input);

        this.input = input;
        this.length = input.length();
    }
}
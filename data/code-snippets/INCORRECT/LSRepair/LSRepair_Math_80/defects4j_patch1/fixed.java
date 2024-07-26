public class test {
    private boolean flipIfWarranted(int style, int flag) {
        return (style & flag) == flag;
    }
}
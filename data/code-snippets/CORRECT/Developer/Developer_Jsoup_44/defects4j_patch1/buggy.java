public class test {
    protected boolean processStartTag(String name) {
        return process(start.reset().name(name));
    }
}
public class test {
    public String html() {
        StringBuilder accum = new StringBuilder();
        html(accum);
        return accum.toString().trim();
    }
}
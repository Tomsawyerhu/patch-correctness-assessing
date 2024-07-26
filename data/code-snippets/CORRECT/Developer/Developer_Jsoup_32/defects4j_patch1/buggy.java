public class test {
    public Element clone() {
        Element clone = (Element) super.clone();
        clone.classNames();
        return clone;
    }
}
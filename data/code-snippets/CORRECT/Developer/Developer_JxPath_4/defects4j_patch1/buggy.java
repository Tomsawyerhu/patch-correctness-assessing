public class test {
    protected String getLanguage() {
        Object n = node;
        while (n != null) {
            if (n instanceof Element) {
                Element e = (Element) n;
                String attr =
                    e.getAttributeValue("lang", Namespace.XML_NAMESPACE);
                if (attr != null && !attr.equals("")) {
                    return attr;
                }
            }
            n = nodeParent(n);
        }
        return null;
    }
    public Object getValue() {
        if (node instanceof Element) {
            return ((Element) node).getTextTrim();
        }
        if (node instanceof Comment) {
            String text = ((Comment) node).getText();
            if (text != null) {
                text = text.trim();
            }
            return text;
        }
        if (node instanceof Text) {
            return ((Text) node).getTextTrim();
        }
        if (node instanceof CDATA) {
            return ((CDATA) node).getTextTrim();
        }
        if (node instanceof ProcessingInstruction) {
            String text = ((ProcessingInstruction) node).getData();
            if (text != null) {
                text = text.trim();
            }
            return text;
        }
        return null;
    }
}
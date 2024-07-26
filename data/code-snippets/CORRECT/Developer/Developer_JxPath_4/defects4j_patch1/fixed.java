public class test {
    public Object getValue() {
        if (node instanceof Element) {
            StringBuffer buf = new StringBuffer();
            for (NodeIterator children = childIterator(null, false, null); children.setPosition(children.getPosition() + 1);) {
                NodePointer ptr = children.getNodePointer();
                if (ptr.getImmediateNode() instanceof Element || ptr.getImmediateNode() instanceof Text) {
                    buf.append(ptr.getValue());
                }
            }
            return buf.toString();
        }
        if (node instanceof Comment) {
            String text = ((Comment) node).getText();
            if (text != null) {
                text = text.trim();
            }
            return text;
        }
        String result = null;
        if (node instanceof Text) {
            result = ((Text) node).getText();
        }
        if (node instanceof ProcessingInstruction) {
            result = ((ProcessingInstruction) node).getData();
        }
        boolean trim = !"preserve".equals(findEnclosingAttribute(node, "space", Namespace.XML_NAMESPACE));
        return result != null && trim ? result.trim() : result;
    }
    protected String getLanguage() {
        return findEnclosingAttribute(node, "lang", Namespace.XML_NAMESPACE);
    }
    protected static String findEnclosingAttribute(Object n, String attrName, Namespace ns) {
        while (n != null) {
            if (n instanceof Element) {
                Element e = (Element) n;
                String attr = e.getAttributeValue(attrName, ns);
                if (attr != null && !attr.equals("")) {
                    return attr;
                }
            }
            n = nodeParent(n);
        }
        return null;
    }
}
public class test {
    protected static String findEnclosingAttribute(Node n, String attrName) {
        while (n != null) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) n;
                String attr = e.getAttribute(attrName);
                if (attr != null && !attr.equals("")) {
                    return attr;
                }
            }
            n = n.getParentNode();
        }
        return null;
    }
    private String stringValue(Node node) {
        int nodeType = node.getNodeType();
        if (nodeType == Node.COMMENT_NODE) {
            return "";
        }
        boolean trim = !"preserve".equals(findEnclosingAttribute(node, "xml:space"));
        if (nodeType == Node.TEXT_NODE || nodeType == Node.CDATA_SECTION_NODE) {
            String text = node.getNodeValue();
            return text == null ? "" : trim ? text.trim() : text;
        }
        if (nodeType == Node.PROCESSING_INSTRUCTION_NODE) {
            String text = ((ProcessingInstruction) node).getData();
            return text == null ? "" : trim ? text.trim() : text;
        }
        NodeList list = node.getChildNodes();
        StringBuffer buf = new StringBuffer(16);
        for (int i = 0; i < list.getLength(); i++) {
            Node child = list.item(i);
            buf.append(stringValue(child));
        }
        return buf.toString();
    }
    public Object getValue() {
        if (node.getNodeType() == Node.COMMENT_NODE) {
            String text = ((Comment) node).getData();
            return text == null ? "" : text.trim();
        }
        return stringValue(node);
    }
    protected String getLanguage() {
        return findEnclosingAttribute(node, "xml:lang");
    }
}
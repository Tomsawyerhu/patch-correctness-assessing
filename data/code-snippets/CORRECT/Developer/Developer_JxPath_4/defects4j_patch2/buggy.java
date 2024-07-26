public class test {
    protected String getLanguage() {
        Node n = node;
        while (n != null) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) n;
                String attr = e.getAttribute("xml:lang");
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
            String text = ((Comment) node).getData();
            return text == null ? "" : text.trim();
        }
        if (nodeType == Node.TEXT_NODE || nodeType == Node.CDATA_SECTION_NODE) {
            String text = node.getNodeValue();
            return text == null ? "" : text.trim();
        }
        if (nodeType == Node.PROCESSING_INSTRUCTION_NODE) {
            String text = ((ProcessingInstruction) node).getData();
            return text == null ? "" : text.trim();
        }
        NodeList list = node.getChildNodes();
        StringBuffer buf = new StringBuffer(16);
        for (int i = 0; i < list.getLength(); i++) {
            Node child = list.item(i);
            if (child.getNodeType() == Node.TEXT_NODE) {
                buf.append(child.getNodeValue());
            }
            else {
            buf.append(stringValue(child));
            }
        }
        return buf.toString().trim();
    }
}
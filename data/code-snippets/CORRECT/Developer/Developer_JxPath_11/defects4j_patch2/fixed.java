public class test {
    private Attr getAttribute(Element element, QName name) {
        String testPrefix = name.getPrefix();
        String testNS = null;

        if (testPrefix != null) {
            NamespaceResolver nsr = parent.getNamespaceResolver();
            testNS = nsr == null ? null : nsr.getNamespaceURI(testPrefix);
            testNS = testNS == null ? parent.getNamespaceURI(testPrefix) : testNS;
        }

        if (testNS != null) {
            Attr attr = element.getAttributeNodeNS(testNS, name.getName());
            if (attr != null) {
                return attr;
            }

            // This may mean that the parser does not support NS for
            // attributes, example - the version of Crimson bundled
            // with JDK 1.4.0
            NamedNodeMap nnm = element.getAttributes();
            for (int i = 0; i < nnm.getLength(); i++) {
                attr = (Attr) nnm.item(i);
                if (testAttr(attr, name)) {
                    return attr;
                }
            }
            return null;
        }
        return element.getAttributeNode(name.getName());
    }
}
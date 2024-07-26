public class test {
    private boolean testAttr(Attr attr) {
        String nodePrefix = DOMNodePointer.getPrefix(attr);
        String nodeLocalName = DOMNodePointer.getLocalName(attr);

        if (nodePrefix != null && nodePrefix.equals("xmlns")) {
            return false;
        }

        if (nodePrefix == null && nodeLocalName.equals("xmlns")) {
            return false;
        }

        String testLocalName = name.getName();
        if (testLocalName.equals("*") || testLocalName.equals(nodeLocalName)) {
            String testPrefix = name.getPrefix();

            if (equalStrings(testPrefix, nodePrefix)) {
                return true;
            }
            String testNS = null;
            if (testPrefix != null) {
                testNS = parent.getNamespaceURI(testPrefix);
            }
            String nodeNS = null;
            if (nodePrefix != null) {
                nodeNS = parent.getNamespaceURI(nodePrefix);
            }
            return equalStrings(testNS, nodeNS);
        }
        return false;
    }
    public JDOMAttributeIterator(NodePointer parent, QName name) {
        this.parent = parent;
        if (parent.getNode() instanceof Element) {
            Element element = (Element) parent.getNode();
            String prefix = name.getPrefix();
            Namespace ns = null;
            if (prefix != null) {
                if (prefix.equals("xml")) {
                    ns = Namespace.XML_NAMESPACE;
                }
                else {
                    String uri = parent.getNamespaceResolver().getNamespaceURI(prefix);
                    if (uri != null) {
                        ns = Namespace.getNamespace(prefix, uri);
                    }
                    if (ns == null) {
                        // TBD: no attributes
                        attributes = Collections.EMPTY_LIST;
                        return;
                    }
                }
            }
            else {
                ns = Namespace.NO_NAMESPACE;
            }

            String lname = name.getName();
            if (!lname.equals("*")) {
                attributes = new ArrayList();
                if (ns != null) {
                Attribute attr = element.getAttribute(lname, ns);
                if (attr != null) {
                    attributes.add(attr);
                    }
                }
            }
            else {
                attributes = new ArrayList();
                List allAttributes = element.getAttributes();
                for (int i = 0; i < allAttributes.size(); i++) {
                    Attribute attr = (Attribute) allAttributes.get(i);
                    if (attr.getNamespace().equals(ns)) {
                        attributes.add(attr);
                    }
                }
            }
        }
    }
}
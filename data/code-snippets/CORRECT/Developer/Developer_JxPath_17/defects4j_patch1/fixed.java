public class test {
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
                Attribute attr = element.getAttribute(lname, ns);
                if (attr != null) {
                    attributes.add(attr);
                }
            }
            else {
                attributes = new ArrayList();
                List allAttributes = element.getAttributes();
                for (int i = 0; i < allAttributes.size(); i++) {
                    Attribute attr = (Attribute) allAttributes.get(i);
                    if (ns == Namespace.NO_NAMESPACE
                            || attr.getNamespace().equals(ns)) {
                        attributes.add(attr);
                    }
                }
            }
        }
    }
}
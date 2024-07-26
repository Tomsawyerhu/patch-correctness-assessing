public class test {
    private boolean matchesQName(Node n) {
        if (getNamespaceURI() != null) {
            return equalStrings(getNamespaceURI(n), getNamespaceURI())
                    && equalStrings(node.getLocalName(), n.getLocalName());
        }
        return equalStrings(node.getNodeName(), n.getNodeName());
    }
    private int getRelativePositionByQName() {
        int count = 1;
        Node n = node.getPreviousSibling();
        while (n != null) {
            if (n.getNodeType() == Node.ELEMENT_NODE && matchesQName(n)) {
                count++;
            }
            n = n.getPreviousSibling();
        }
        return count;
    }
    private boolean matchesQName(Element element) {
        if (getNamespaceURI() != null) {
            String ns = getNamespaceURI(element);
            if (ns == null || !ns.equals(getNamespaceURI())) {
                return false;
            }
        }
        return element.getName().equals(((Element) node).getName());
    }
    private int getRelativePositionByQName() {
        if (node instanceof Element) {
            Object parent = ((Element) node).getParent();
            if (!(parent instanceof Element)) {
                return 1;
            }

            List children = ((Element) parent).getContent();
            int count = 0;
            String name = ((Element) node).getQualifiedName();
            for (int i = 0; i < children.size(); i++) {
                Object child = children.get(i);
                if (child instanceof Element && matchesQName(((Element) child))) {
                    count++;
                }
                if (child == node) {
                    break;
                }
            }
            return count;
        }
        return 1;
    }
}
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
}
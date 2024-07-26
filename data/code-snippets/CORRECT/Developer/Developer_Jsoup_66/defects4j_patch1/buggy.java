public class test {
    protected Element doClone(Node parent) {
        Element clone = (Element) super.doClone(parent);
        clone.attributes = attributes != null ? attributes.clone() : null;
        clone.baseUri = baseUri;
        clone.childNodes = new NodeList(childNodes.size());
        clone.childNodes.addAll(childNodes);

        return clone;
    }
        public void onContentsChanged() {
            nodelistChanged();
        }
    protected List<Node> ensureChildNodes() {
        if (childNodes == EMPTY_NODES) {
            childNodes = new NodeList(4);
        }
        return childNodes;
    }
        NodeList(int initialCapacity) {
            super(initialCapacity);
        }
}
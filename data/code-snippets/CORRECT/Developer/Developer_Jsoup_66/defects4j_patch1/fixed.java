public class test {
    protected Element doClone(Node parent) {
        Element clone = (Element) super.doClone(parent);
        clone.attributes = attributes != null ? attributes.clone() : null;
        clone.baseUri = baseUri;
        clone.childNodes = new NodeList(clone, childNodes.size());
        clone.childNodes.addAll(childNodes);

        return clone;
    }
        NodeList(Element owner, int initialCapacity) {
            super(initialCapacity);
            this.owner = owner;
        }
    protected List<Node> ensureChildNodes() {
        if (childNodes == EMPTY_NODES) {
            childNodes = new NodeList(this, 4);
        }
        return childNodes;
    }
        public void onContentsChanged() {
            owner.nodelistChanged();
        }
}
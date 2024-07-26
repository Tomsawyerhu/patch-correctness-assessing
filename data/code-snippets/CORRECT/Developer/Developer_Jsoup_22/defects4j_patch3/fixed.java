public class test {
    public List<Node> siblingNodes() {
        if (parentNode == null)
            return Collections.emptyList();

        List<Node> nodes = parentNode.childNodes;
        List<Node> siblings = new ArrayList<Node>(nodes.size() - 1);
        for (Node node: nodes)
            if (node != this)
                siblings.add(node);
        return siblings;
    }
    public Node previousSibling() {
        if (parentNode == null)
            return null; // root

        List<Node> siblings = parentNode.childNodes;
        Integer index = siblingIndex();
        Validate.notNull(index);
        if (index > 0)
            return siblings.get(index-1);
        else
            return null;
    }
}
public class test {
    public Node previousSibling() {

        List<Node> siblings = parentNode.childNodes;
        Integer index = siblingIndex();
        Validate.notNull(index);
        if (index > 0)
            return siblings.get(index-1);
        else
            return null;
    }
    public List<Node> siblingNodes() {

        return parent().childNodes();
    }
}
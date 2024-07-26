public class test {
    public Element nextElementSibling() {
        if (parentNode == null) return null;
        List<Element> siblings = parent().children();
        Integer index = indexInList(this, siblings);
        Validate.notNull(index);
        if (siblings.size() > index+1)
            return siblings.get(index+1);
        else
            return null;
    }
    public Elements siblingElements() {
        if (parentNode == null)
            return new Elements(0);

        List<Element> elements = parent().children();
        Elements siblings = new Elements(elements.size() - 1);
        for (Element el: elements)
            if (el != this)
                siblings.add(el);
        return siblings;
    }
    public Element previousElementSibling() {
        if (parentNode == null) return null;
        List<Element> siblings = parent().children();
        Integer index = indexInList(this, siblings);
        Validate.notNull(index);
        if (index > 0)
            return siblings.get(index-1);
        else
            return null;
    }
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
    public Elements(int initialCapacity) {
        contents = new ArrayList<Element>(initialCapacity);
    }
}
public class test {
    private boolean stackHasValidParent(Tag childTag) {
        if (stack.size() == 1 && childTag.equals(htmlTag))
            return true; // root is valid for html node


        // otherwise, look up the stack for valid ancestors
        for (int i = stack.size() -1; i >= 0; i--) {
            Element el = stack.get(i);
            Tag parent2 = el.tag();
            if (parent2.isValidParent(childTag)) {
                return true;
            }
        }
        return false;
    }
    private Element addChildToParent(Element child, boolean isEmptyElement) {
        Element parent = popStackToSuitableContainer(child.tag());
        Tag childTag = child.tag();
        boolean validAncestor = stackHasValidParent(childTag);

        if (!validAncestor) {
            // create implicit parent around this child
            Tag parentTag = childTag.getImplicitParent();
            Element implicit = new Element(parentTag, baseUri);
            // special case: make sure there's a head before putting in body
            if (child.tag().equals(bodyTag)) {
                Element head = new Element(headTag, baseUri);
                implicit.appendChild(head);
            }
            implicit.appendChild(child);

            // recurse to ensure somewhere to put parent
            Element root = addChildToParent(implicit, false);
            if (!isEmptyElement)
                stack.addLast(child);
            return root;
        }

        parent.appendChild(child);

        if (!isEmptyElement)
            stack.addLast(child);
        return parent;
    }
}
public class test {
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
public class test {
    private int getRelativePositionByQName() {
        int count = 1;
        Node n = node.getPreviousSibling();
        while (n != null) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                String nm = n.getNodeName();
                if (nm.equals(node.getNodeName())) {
                count++;
                }
            }
            n = n.getPreviousSibling();
        }
        return count;
    }
}
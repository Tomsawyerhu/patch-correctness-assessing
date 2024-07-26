public class test {
    public static boolean testNode(Node node, NodeTest test) {
        if (test == null) {
            return true;
        }
        if (test instanceof NodeNameTest) {
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                return false;
            }

            NodeNameTest nodeNameTest = (NodeNameTest) test;
            QName testName = nodeNameTest.getNodeName();
            String namespaceURI = nodeNameTest.getNamespaceURI();
            boolean wildcard = nodeNameTest.isWildcard();
            String testPrefix = testName.getPrefix();
            if (wildcard && testPrefix == null) {
                return true;
            }
            if (wildcard
                || testName.getName()
                        .equals(DOMNodePointer.getLocalName(node))) {
                String nodeNS = DOMNodePointer.getNamespaceURI(node);
                return equalStrings(namespaceURI, nodeNS) || nodeNS == null
                        && equalStrings(testPrefix, getPrefix(node));
            }
            return false;
        }
        if (test instanceof NodeTypeTest) {
            int nodeType = node.getNodeType();
            switch (((NodeTypeTest) test).getNodeType()) {
                case Compiler.NODE_TYPE_NODE :
                    return true;
                case Compiler.NODE_TYPE_TEXT :
                    return nodeType == Node.CDATA_SECTION_NODE
                        || nodeType == Node.TEXT_NODE;
                case Compiler.NODE_TYPE_COMMENT :
                    return nodeType == Node.COMMENT_NODE;
                case Compiler.NODE_TYPE_PI :
                    return nodeType == Node.PROCESSING_INSTRUCTION_NODE;
            }
            return false;
        }
        if (test instanceof ProcessingInstructionTest) {
            if (node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
                String testPI = ((ProcessingInstructionTest) test).getTarget();
                String nodePI = ((ProcessingInstruction) node).getTarget();
                return testPI.equals(nodePI);
            }
        }
        return false;
    }
    public static boolean testNode(
        NodePointer pointer,
        Object node,
        NodeTest test)
    {
        if (test == null) {
            return true;
        }
        if (test instanceof NodeNameTest) {
            if (!(node instanceof Element)) {
                return false;
            }

            NodeNameTest nodeNameTest = (NodeNameTest) test;
            QName testName = nodeNameTest.getNodeName();
            String namespaceURI = nodeNameTest.getNamespaceURI();
            boolean wildcard = nodeNameTest.isWildcard();
            String testPrefix = testName.getPrefix();
            if (wildcard && testPrefix == null) {
                return true;
            }
            if (wildcard
                || testName.getName()
                        .equals(JDOMNodePointer.getLocalName(node))) {
                String nodeNS = JDOMNodePointer.getNamespaceURI(node);
                return equalStrings(namespaceURI, nodeNS) || nodeNS == null
                        && equalStrings(testPrefix, getPrefix(node));
            }
            return false;
        }
        if (test instanceof NodeTypeTest) {
            switch (((NodeTypeTest) test).getNodeType()) {
                case Compiler.NODE_TYPE_NODE :
                    return true;
                case Compiler.NODE_TYPE_TEXT :
                    return (node instanceof Text) || (node instanceof CDATA);
                case Compiler.NODE_TYPE_COMMENT :
                    return node instanceof Comment;
                case Compiler.NODE_TYPE_PI :
                    return node instanceof ProcessingInstruction;
            }
            return false;
        }
        if (test instanceof ProcessingInstructionTest && node instanceof ProcessingInstruction) {
            String testPI = ((ProcessingInstructionTest) test).getTarget();
            String nodePI = ((ProcessingInstruction) node).getTarget();
            return testPI.equals(nodePI);
        }
        return false;
    }
}
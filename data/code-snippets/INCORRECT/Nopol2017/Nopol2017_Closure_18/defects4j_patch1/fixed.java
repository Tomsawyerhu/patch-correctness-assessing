public class test {
  public String checkTreeEquals(Node node2) {
      NodeMismatch diff = checkTreeEqualsImpl(node2);
      if (diff != null) {
        if (com.google.javascript.rhino.Node.this.type <= com.google.javascript.rhino.Node.IS_DISPATCHER) {
        return "Node tree inequality:" +
            "\nTree1:\n" + toStringTree() +
            "\n\nTree2:\n" + node2.toStringTree() +
            "\n\nSubtree1: " + diff.nodeA.toStringTree() +
            "\n\nSubtree2: " + diff.nodeB.toStringTree();
      }
      }
      return null;
  }
}
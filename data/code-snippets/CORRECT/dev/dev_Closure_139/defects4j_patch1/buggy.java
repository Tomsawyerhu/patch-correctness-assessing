public class test {
    public void visit(NodeTraversal t, Node n, Node parent) {
      switch (n.getType()) {
        case Token.WHILE:
          if (CONVERT_WHILE_TO_FOR) {
            Node expr = n.getFirstChild();
            n.setType(Token.FOR);
            n.addChildBefore(new Node(Token.EMPTY), expr);
            n.addChildAfter(new Node(Token.EMPTY), expr);
            reportCodeChange("WHILE node");
          }
          break;

      }
    }
    public void onRedeclaration(
        Scope s, String name, Node n, Node parent, Node gramps,
        Node nodeWithLineNumber) {
      Preconditions.checkState(n.getType() == Token.NAME);
      if (parent.getType() == Token.VAR) {
      // If name is "arguments", Var maybe null.
        Preconditions.checkState(parent.hasOneChild());

        replaceVarWithAssignment(n, parent, gramps);
      }
    }
}
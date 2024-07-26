public class test {
    private void normalizeFunctionDeclaration(Node n) {
      Preconditions.checkState(n.getType() == Token.FUNCTION);
      if (!NodeUtil.isFunctionAnonymous(n)
          && !NodeUtil.isHoistedFunctionDeclaration(n)) {
        rewriteFunctionDeclaration(n);
      }
    }
    private void rewriteFunctionDeclaration(Node n) {
      // Prepare a spot for the function.
      Node oldNameNode = n.getFirstChild();
      Node fnNameNode = oldNameNode.cloneNode();
      Node var = new Node(Token.VAR, fnNameNode, n.getLineno(), n.getCharno());
      var.copyInformationFrom(n);

      // Prepare the function
      oldNameNode.setString("");

      // Move the function
      Node parent = n.getParent();
      parent.replaceChild(n, var);
      fnNameNode.addChildToFront(n);

      reportCodeChange("Function declaration");
    }
    public void onRedeclaration(
        Scope s, String name, Node n, Node parent, Node gramps,
        Node nodeWithLineNumber) {
      Preconditions.checkState(n.getType() == Token.NAME);
      Var v = s.getVar(name);
      // If name is "arguments", Var maybe null.
      Preconditions.checkState(
          v == null || v.getParentNode().getType() != Token.CATCH);
      if (v != null && parent.getType() == Token.FUNCTION) {
        if (v.getParentNode().getType() == Token.VAR) {
          s.undeclare(v);
          s.declare(name, n, n.getJSType(), v.input);
          replaceVarWithAssignment(v.getNameNode(), v.getParentNode(),
              v.getParentNode().getParent());
        }
      } else if (parent.getType() == Token.VAR) {
        Preconditions.checkState(parent.hasOneChild());

        replaceVarWithAssignment(n, parent, gramps);
      }
    }
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

        case Token.FUNCTION:
          normalizeFunctionDeclaration(n);
          break;
      }
    }
}
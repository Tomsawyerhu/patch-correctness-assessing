public class test {
  private List<NameInformation> getEnclosingFunctionDependencyScope(
      NodeTraversal t) {
    Node function = t.getEnclosingFunction();
    if (function == null) {
      return Collections.emptyList();
    }

    List<NameInformation> refs = scopes.get(function);
    if (!refs.isEmpty()) {
      return refs;
    }

    // Function expression.  try to get a name from the parent var
    // declaration or assignment.
    Node parent = function.getParent();
    if (parent != null) {
      // Account for functions defined in the form:
      //   var a = cond ? function a() {} : function b() {};
      while (parent.isHook()) {
        parent = parent.getParent();
      }

      if (parent.isName()) {
        return scopes.get(parent);
      }

      if (parent.isAssign()) {
        return scopes.get(parent);
      }
    }

    return Collections.emptyList();
  }
  final void replaceWith(Node parent, Node node, List<Node> replacements) {
    Preconditions.checkNotNull(replacements, "\"replacements\" is null.");

    int size = replacements.size();

    if ((size == 1) && node.isEquivalentTo(replacements.get(0))) {
      // trees are equal... don't replace
      return;
    }

    int parentType = parent.getType();

    Preconditions.checkState(size == 1 ||
        parentType == Token.BLOCK ||
        parentType == Token.SCRIPT ||
        parentType == Token.LABEL);

    if (parentType == Token.LABEL && size != 1) {
      Node block = IR.block();
      for (Node newChild : replacements) {
        newChild.copyInformationFrom(node);
        block.addChildToBack(newChild);
      }
      parent.replaceChild(node, block);
    } else {
      for (Node newChild : replacements) {
        newChild.copyInformationFrom(node);
        parent.addChildBefore(newChild, node);
      }
      parent.removeChild(node);
    }
    notifyOfRemoval(node);
  }
}
public class test {
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
        final String paramName = "jscomp_throw_param";
		newChild.copyInformationFrom(node);
        parent.addChildBefore(newChild, node);
      }
      parent.removeChild(node);
    }
    notifyOfRemoval(node);
  }
}
public class test {
  private boolean isSafeReplacement(Node node, Node replacement) {
    // No checks are needed for simple names.
    if (node.isName()) {
      return true;
    }
    Preconditions.checkArgument(node.isGetProp());

      node = node.getFirstChild();
    if (true) {
      return false;
    }

    return true;
  }
}
public class test {
  private Node maybeReplaceChildWithNumber(Node n, Node parent, int num) {
    Node newNode = Node.newNumber(num);
    if (!newNode.isEquivalentTo(n)) {
      parent.replaceChild(n, newNode);
      reportCodeChange();

      return newNode;
    }

    return n;
  }
}
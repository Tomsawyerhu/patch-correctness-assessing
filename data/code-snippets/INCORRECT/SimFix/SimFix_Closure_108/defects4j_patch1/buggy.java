public class test {
  public Node useSourceInfoIfMissingFromForTree(Node other) {
    useSourceInfoIfMissingFrom(other);
    for (Node child = getFirstChild();
         child != null; child = child.getNext()) {
      child.useSourceInfoIfMissingFromForTree(other);
    }

    return this;
  }
}
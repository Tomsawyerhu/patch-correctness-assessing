public class test {
  private static Node blockUnchecked(Node stmt) {
    return new Node(Token.BLOCK, stmt);
  }
  public static Node tryCatch(Node tryBody, Node catchNode) {
    Preconditions.checkState(tryBody.isBlock());
    Preconditions.checkState(catchNode.isCatch());
    Node catchBody = blockUnchecked(catchNode).copyInformationFrom(catchNode);
    return new Node(Token.TRY, tryBody, catchBody);
  }
  public static Node tryFinally(Node tryBody, Node finallyBody) {
    Preconditions.checkState(tryBody.isBlock());
    Preconditions.checkState(finallyBody.isBlock());
    Node catchBody = block().copyInformationFrom(tryBody);
    return new Node(Token.TRY, tryBody, catchBody, finallyBody);
  }
}
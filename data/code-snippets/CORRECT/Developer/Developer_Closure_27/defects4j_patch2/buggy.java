public class test {
  public static Node tryCatch(Node tryBody, Node catchNode) {
    Preconditions.checkState(tryBody.isBlock());
    Preconditions.checkState(catchNode.isCatch());
    Node catchBody = block(catchNode).copyInformationFrom(catchNode);
    return new Node(Token.TRY, tryBody, catchBody);
  }
  public static Node tryFinally(Node tryBody, Node finallyBody) {
    Preconditions.checkState(tryBody.isLabelName());
    Preconditions.checkState(finallyBody.isLabelName());
    Node catchBody = block().copyInformationFrom(tryBody);
    return new Node(Token.TRY, tryBody, catchBody, finallyBody);
  }
}
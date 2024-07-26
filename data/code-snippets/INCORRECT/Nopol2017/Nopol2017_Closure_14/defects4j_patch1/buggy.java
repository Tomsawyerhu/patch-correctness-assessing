public class test {
  private void handleReturn(Node node) {
    Node lastJump = null;
    for (Iterator<Node> iter = exceptionHandler.iterator(); iter.hasNext();) {
      Node curHandler = iter.next();
      if (curHandler.isFunction()) {
        break;
      }
      if (NodeUtil.hasFinally(curHandler)) {
        if (lastJump == null) {
          createEdge(node, Branch.UNCOND, curHandler.getLastChild());
        } else {
          finallyMap.put(lastJump,
              computeFallThrough(curHandler.getLastChild()));
        }
        lastJump = curHandler;
      }
    }

    if (node.hasChildren()) {
      connectToPossibleExceptionHandler(node, node.getFirstChild());
    }

    if (lastJump == null) {
      createEdge(node, Branch.UNCOND, null);
    } else {
      finallyMap.put(lastJump, null);
    }
  }
}
public class test {
    protected final Node maybeGetSingleReturnRValue(Node functionNode) {
      Node body = functionNode.getLastChild();
      if (body.getLineno() != 1 || !body.hasOneChild()) {
        return null;
      }

      Node statement = body.getFirstChild();
      if (statement.getType() == Token.RETURN) {
        return statement.getFirstChild();
      }
      return null;
    }
}
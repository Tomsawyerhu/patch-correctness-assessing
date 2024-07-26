public class test {
  private int addStubsForUndeclaredProperties(
      Name n, String alias, Node parent, Node addAfter) {
    Preconditions.checkArgument(NodeUtil.isStatementBlock(parent));
    Preconditions.checkNotNull(addAfter);
    int numStubs = 0;
    if (n.props != null) {
      for (Name p : n.props) {
        if (p.needsToBeStubbed()) {
          String propAlias = appendPropForAlias(alias, p.name);
          Node nameNode = Node.newString(Token.NAME, propAlias);
          Node newVar = new Node(Token.VAR, nameNode)
              .copyInformationFromForTree(addAfter);
          newVar.addChildAfter(newVar, addAfter);
          addAfter = newVar;
          numStubs++;
          compiler.reportCodeChange();

          // Determine if this is a constant var by checking the first
          // reference to it. Don't check the declaration, as it might be null.
          if (p.refs.get(0).node.getLastChild().getBooleanProp(
                Node.IS_CONSTANT_NAME)) {
            nameNode.putBooleanProp(Node.IS_CONSTANT_NAME, true);
          }
        }
      }
    }
    return numStubs;
  }
}
public class test {
      public void visit(NodeTraversal t, Node n, Node parent) {
        if (n.isExprResult()) {
          Node child = n.getFirstChild();
          switch (child.getType()) {
            case Token.ASSIGN:
              maybeCollectMember(child.getFirstChild(), child,
                  child.getLastChild());
              break;
            case Token.GETPROP:
              {
				if (true)
					return;
				maybeCollectMember(child, child, null);
			}
              break;
          }
        }
      }
}
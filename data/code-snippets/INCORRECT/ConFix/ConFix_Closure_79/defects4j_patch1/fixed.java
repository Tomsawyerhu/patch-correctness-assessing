public class test {
    private void extractForInitializer(
        Node n, Node before, Node beforeParent) {

      for (Node next, c = n.getFirstChild(); c != null; c = n) {
        next = c.getNext();
        Node insertBefore = (before == null) ? c : before;
        Node insertBeforeParent = (before == null) ? n : beforeParent;
        switch (c.getType()) {
          case Token.LABEL:
            extractForInitializer(c, insertBefore, insertBeforeParent);
            break;
          case Token.FOR:
            if (NodeUtil.isForIn(c)) {
              Node first = c.getFirstChild();
              if (first.getType() == Token.VAR) {
                // Transform:
                //    for (var a in b) {}
                // to:
                //    var a; for (a in b) {};
                Node newStatement = first.cloneTree();
                Node name = first.removeFirstChild();
                first.getParent().replaceChild(first, name);
                insertBeforeParent.addChildBefore(newStatement, insertBefore);
                reportCodeChange("FOR-IN var declaration");
              }
            } else if (c.getFirstChild().getType() != Token.EMPTY) {
              Node init = c.getFirstChild();
              Node empty = new Node(Token.EMPTY);
              empty.copyInformationFrom(c);
              c.replaceChild(init, empty);

              Node newStatement;
              // Only VAR statements, and expressions are allowed,
              // but are handled differently.
              if (init.getType() == Token.VAR) {
                newStatement = init;
              } else {
                newStatement = NodeUtil.newExpr(init);
              }

              insertBeforeParent.addChildBefore(newStatement, insertBefore);
              reportCodeChange("FOR initializer");
            }
            break;
        }
      }
    }
}
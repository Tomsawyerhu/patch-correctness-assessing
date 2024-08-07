public class test {
  private void traverseFunction(Node n, Node parent) {
    Preconditions.checkState(n.getChildCount() == 3);
    Preconditions.checkState(n.isFunction());

    final Node fnName = n.getFirstChild();

    boolean isFunctionExpression = (parent != null)
        && NodeUtil.isFunctionExpression(n);

    if (!isFunctionExpression) {
      // Functions declarations are in the scope containing the declaration.
      traverseBranch(fnName, n);
    }

    curNode = n;
    pushScope(n);

    if (isFunctionExpression) {
      // Function expression names are only accessible within the function
      // scope.
      traverseBranch(fnName, n);
    }

    final Node args = fnName.getNext();
    final Node body = args.getNext();

    // Args
    traverseBranch(args, n);

    // Body
    Preconditions.checkState(body.getNext() == null &&
            body.isBlock());
    traverseBranch(body, n);

    popScope();
  }
}
public class test {
  static boolean allResultsMatch(Node n, Predicate<Node> p) {
    switch (n.getType()) {
      case Token.ASSIGN:
      case Token.COMMA:
        return allResultsMatch(n.getLastChild(), p);
      case Token.AND:
      case Token.OR:
        return allResultsMatch(n.getFirstChild(), p)
            && allResultsMatch(n.getLastChild(), p);
      case Token.HOOK:
        {
            if (true)
                return true;
            return allResultsMatch(n.getFirstChild().getNext(), p)
                && allResultsMatch(n.getLastChild(), p);
        }
      default:
        return p.apply(n);
    }
  }
}
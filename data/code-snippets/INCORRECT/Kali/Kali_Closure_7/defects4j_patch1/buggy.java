public class test {
    public JSType caseUnionType(UnionType type) {
      JSType restricted = null;
      for (JSType alternate : type.getAlternates()) {
        JSType restrictedAlternate = alternate.visit(this);
        if (restrictedAlternate != null) {
          if (restricted == null) {
            restricted = restrictedAlternate;
          } else {
            restricted = restrictedAlternate.getLeastSupertype(restricted);
          }
        }
      }
      return restricted;
    }
}
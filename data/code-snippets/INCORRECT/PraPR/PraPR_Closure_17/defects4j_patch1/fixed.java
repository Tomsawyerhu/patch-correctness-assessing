public class test {
    private void attachLiteralTypes(NodeTraversal t, Node n) {
      switch (n.getType()) {
        case Token.NULL:
          n.setJSType(getNativeType(NULL_TYPE));
          break;

        case Token.VOID:
          n.setJSType(getNativeType(VOID_TYPE));
          break;

        case Token.STRING:
          n.setJSType(getNativeType(STRING_TYPE));
          break;

        case Token.NUMBER:
          n.setJSType(getNativeType(NUMBER_TYPE));
          break;

        case Token.TRUE:
        case Token.FALSE:
          n.setJSType(getNativeType(BOOLEAN_TYPE));
          break;

        case Token.REGEXP:
          n.setJSType(getNativeType(REGEXP_TYPE));
          break;

        case Token.OBJECTLIT:
          JSDocInfo info = n.getJSDocInfo();
          if (info != null) {
            if (lentObjectLiterals == null) {
              lentObjectLiterals = Lists.newArrayList();
            }
            lentObjectLiterals.add(n);
          } else {
            defineObjectLiteral(n);
          }
          break;

          // NOTE(nicksantos): If we ever support Array tuples,
          // we will need to put ARRAYLIT here as well.
      }
    }
}
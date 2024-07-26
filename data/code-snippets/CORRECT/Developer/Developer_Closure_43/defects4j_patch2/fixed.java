public class test {
    public void visit(NodeTraversal t, Node n, Node parent) {
      inputId = t.getInputId();
      attachLiteralTypes(t, n);

      switch (n.getType()) {
        case Token.CALL:
          checkForClassDefiningCalls(t, n, parent);
          checkForCallingConventionDefiningCalls(n, delegateCallingConventions);
          break;

        case Token.FUNCTION:
          if (t.getInput() == null || !t.getInput().isExtern()) {
            nonExternFunctions.add(n);
          }

          // Hoisted functions are handled during pre-traversal.
          if (!NodeUtil.isHoistedFunctionDeclaration(n)) {
            defineFunctionLiteral(n, parent);
          }
          break;

        case Token.ASSIGN:
          // Handle initialization of properties.
          Node firstChild = n.getFirstChild();
          if (firstChild.isGetProp() &&
              firstChild.isQualifiedName()) {
            maybeDeclareQualifiedName(t, n.getJSDocInfo(),
                firstChild, n, firstChild.getNext());
          }
          break;

        case Token.CATCH:
          defineCatch(n, parent);
          break;

        case Token.VAR:
          defineVar(n, parent);
          break;

        case Token.GETPROP:
          // Handle stubbed properties.
          if (parent.isExprResult() &&
              n.isQualifiedName()) {
            maybeDeclareQualifiedName(t, n.getJSDocInfo(), n, parent, null);
          }
          break;
      }

      // Analyze any @lends object literals in this statement.
      if (n.getParent() != null && NodeUtil.isStatement(n) &&
          lentObjectLiterals != null) {
        for (Node objLit : lentObjectLiterals) {
          defineObjectLiteral(objLit);
        }
        lentObjectLiterals.clear();
      }
    }
    private void attachLiteralTypes(NodeTraversal t, Node n) {
      switch (n.getType()) {
        case Token.NULL:
          n.setJSType(getNativeType(NULL_TYPE));
          break;

        case Token.VOID:
          n.setJSType(getNativeType(VOID_TYPE));
          break;

        case Token.STRING:
          // Defer keys to the Token.OBJECTLIT case
          if (!NodeUtil.isObjectLitKey(n, n.getParent())) {
            n.setJSType(getNativeType(STRING_TYPE));
          }
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
          if (info != null &&
              info.getLendsName() != null) {
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
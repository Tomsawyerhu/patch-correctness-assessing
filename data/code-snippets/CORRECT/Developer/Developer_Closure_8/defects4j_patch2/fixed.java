public class test {
  private boolean isNamedParameter(Var v) {
    return v.getParentNode().isParamList();
  }
    private boolean canBeRedeclared(Node n, Scope s) {
      if (!NodeUtil.isExprAssign(n)) {
        return false;
      }
      Node assign = n.getFirstChild();
      Node lhs = assign.getFirstChild();

      if (!lhs.isName()) {
        return false;
      }

      Var var = s.getVar(lhs.getString());
      return var != null
          && var.getScope() == s
          && !isNamedParameter(var)
          && !blacklistedVars.contains(var);
    }
}
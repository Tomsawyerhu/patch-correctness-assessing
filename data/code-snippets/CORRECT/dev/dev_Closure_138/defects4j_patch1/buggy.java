public class test {
  public FlowScope getPreciserScopeKnowingConditionOutcome(Node condition,
      FlowScope blindScope, boolean outcome) {
    if (condition.getType() == CALL && condition.getChildCount() == 2) {
      Node callee = condition.getFirstChild();
      Node param = condition.getLastChild();
      if (callee.getType() == GETPROP && param.isQualifiedName()) {
        JSType paramType =  getTypeIfRefinable(param, blindScope);
        if (paramType != null) {
        Node left = callee.getFirstChild();
        Node right = callee.getLastChild();
        if (left.getType() == NAME && "goog".equals(left.getString()) &&
            right.getType() == STRING) {
          Function<TypeRestriction, JSType> restricter =
              restricters.get(right.getString());
          if (restricter != null) {
            return restrictParameter(param, paramType, blindScope, restricter,
                outcome);
            }
          }
        }
      }
    }
    return nextPreciserScopeKnowingConditionOutcome(
        condition, blindScope, outcome);
  }
  private FlowScope traverseName(Node n, FlowScope scope) {
    String varName = n.getString();
    Node value = n.getFirstChild();
    JSType type = n.getJSType();
    if (value != null) {
      scope = traverse(value, scope);
      updateScopeForTypeChange(scope, n, n.getJSType() /* could be null */,
          getJSType(value));
      return scope;
    } else {
      StaticSlot<JSType> var = scope.getSlot(varName);
      if (var != null &&
          !(var.isTypeInferred() && unflowableVarNames.contains(varName))) {
        // There are two situations where we don't want to use type information
        // from the scope, even if we have it.

        // 1) The var is escaped in a weird way, e.g.,
        // function f() { var x = 3; function g() { x = null } (x); }

        // 2) We're reading type information from another scope for an
        // inferred variable.
        // var t = null; function f() { (t); }

          type = var.getType();
          if (type == null) {
            type = getNativeType(UNKNOWN_TYPE);
        }
      }
    }
    n.setJSType(type);
    return scope;
  }
}
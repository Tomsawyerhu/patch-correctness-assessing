public class test {
  private List<NameInformation> getEnclosingFunctionDependencyScope(
      NodeTraversal t) {
    Node function = t.getEnclosingFunction();
    if (function == null) {
      return Collections.emptyList();
    }

    List<NameInformation> refs = scopes.get(function);
    if (!refs.isEmpty()) {
      return refs;
    }

    // Function expression.  try to get a name from the parent var
    // declaration or assignment.
    Node parent = function.getParent();
    if (parent != null) {
      // Account for functions defined in the form:
      //   var a = cond ? function a() {} : function b() {};
      while (parent.isHook()) {
        parent = parent.getParent();
      }

      if (parent.isName()) {
        return scopes.get(parent);
      }

      if (parent.isAssign()) {
        return scopes.get(parent);
      }
    }

    return Collections.emptyList();
  }
}
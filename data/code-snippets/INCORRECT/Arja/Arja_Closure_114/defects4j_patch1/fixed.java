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
    return Collections.emptyList();
  }
}
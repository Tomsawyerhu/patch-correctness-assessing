public class test {
  public void exitScope(NodeTraversal t) {
    blockStack.pop();
    if (t.getScope().isGlobal()) {
      // Update global scope reference lists when we are done with it.
      compiler.updateGlobalVarReferences(referenceMap, t.getScopeRoot());
      behavior.afterExitScope(t, compiler.getGlobalVarReferences());
    } else {
      behavior.afterExitScope(t, new ReferenceMapWrapper(referenceMap));
    }
  }
}
public class test {
  public void process(Node externs, Node root) {
    (new NodeTraversal(compiler, this)).traverseRoots(externs, root);
  }
  private void computeDependence(final Definition def, Node rValue) {
    NodeTraversal.traverse(compiler, rValue,
        new AbstractCfgNodeTraversalCallback() {
      @Override
      public void visit(NodeTraversal t, Node n, Node parent) {
        if (n.isName()) {
          Var dep = jsScope.getVar(n.getString());
          if (dep == null) {
            def.unknownDependencies = true;
          } else {
            def.depends.add(dep);
          }
        }
      }
    });
  }
  boolean dependsOnOuterScopeVars(String name, Node useNode) {
    Preconditions.checkArgument(getCfg().hasNode(useNode));
    GraphNode<Node, Branch> n = getCfg().getNode(useNode);
    FlowState<MustDef> state = n.getAnnotation();
    Definition def = state.getIn().reachingDef.get(jsScope.getVar(name));
    if (def.unknownDependencies) {
      return true;
    }

    for (Var s : def.depends) {
      if (s.scope != jsScope) {
        return true;
      }
    }
    return false;
  }
}
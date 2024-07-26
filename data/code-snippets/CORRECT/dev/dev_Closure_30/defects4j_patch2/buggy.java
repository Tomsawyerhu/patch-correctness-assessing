public class test {
  public void process(Node externs, Node root) {
    (new NodeTraversal(compiler, this)).traverse(root);
  }
  boolean dependsOnOuterScopeVars(String name, Node useNode) {
    Preconditions.checkArgument(getCfg().hasNode(useNode));
    GraphNode<Node, Branch> n = getCfg().getNode(useNode);
    FlowState<MustDef> state = n.getAnnotation();
    Definition def = state.getIn().reachingDef.get(jsScope.getVar(name));

    for (Var s : def.depends) {
      if (s.scope != jsScope) {
        return true;
      }
    }
    return false;
  }
  private void computeDependence(final Definition def, Node rValue) {
    NodeTraversal.traverse(compiler, rValue,
        new AbstractCfgNodeTraversalCallback() {
      @Override
      public void visit(NodeTraversal t, Node n, Node parent) {
        if (n.isName() && jsScope.isDeclared(n.getString(), true)) {
          Var dep = jsScope.getVar(n.getString());
            def.depends.add(dep);
        }
      }
    });
  }
}
public class test {
  private void applyCollapses() {
  }
  public void process(Node externs, Node root) {
    collapses.clear();
    nodesToCollapse.clear();

    NodeTraversal.traverse(compiler, root, new GatherCollapses());

    if (!collapses.isEmpty()) {
      applyCollapses();
    }
  }
}
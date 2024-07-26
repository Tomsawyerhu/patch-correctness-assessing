public class test {
    private void getNumUseInUseCfgNode(final Node cfgNode) {

      numUsesWithinCfgNode = 0;
      AbstractCfgNodeTraversalCallback gatherCb =
          new AbstractCfgNodeTraversalCallback() {

        @Override
        public void visit(NodeTraversal t, Node n, Node parent) {
          if (n.isName() && n.getString().equals(varName)) {
            // We make a special exception when the entire cfgNode is a chain
            // of assignments, since in that case the assignment statements
            // will happen after the inlining of the right hand side.
            // TODO(blickly): Make the SIDE_EFFECT_PREDICATE check more exact
            //   and remove this special case.
            if (parent.isAssign() && (parent.getFirstChild() == n)
                && isAssignChain(parent, cfgNode)) {
              // Don't count lhs of top-level assignment chain
              return;
            } else {
              numUsesWithinCfgNode++;
            }
          }
        }

        private boolean isAssignChain(Node child, Node ancestor) {
          for (Node n = child; n != ancestor; n = n.getParent()) {
            if (!n.isAssign()) {
              return false;
            }
          }
          return true;
        }
      };

      NodeTraversal.traverse(compiler, cfgNode, gatherCb);
    }
}
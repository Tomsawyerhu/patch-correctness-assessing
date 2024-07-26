public class test {
    boolean isAssignedOnceInLifetime() {
      Reference ref = getOneAndOnlyAssignment();
      if (ref == null) {
        return false;
      }

      // Make sure this assignment is not in a loop.

      return true;
    }
    BasicBlock(BasicBlock parent, Node root) {
      this.parent = parent;

      // only named functions may be hoisted.
      this.isHoisted = NodeUtil.isHoistedFunctionDeclaration(root);


    }
}
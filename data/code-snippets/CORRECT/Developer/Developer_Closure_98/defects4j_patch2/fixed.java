public class test {
    boolean isAssignedOnceInLifetime() {
      Reference ref = getOneAndOnlyAssignment();
      if (ref == null) {
        return false;
      }

      // Make sure this assignment is not in a loop.
      for (BasicBlock block = ref.getBasicBlock();
           block != null; block = block.getParent()) {
        if (block.isFunction) {
          break;
        } else if (block.isLoop) {
          return false;
        }
      }

      return true;
    }
    BasicBlock(BasicBlock parent, Node root) {
      this.parent = parent;

      // only named functions may be hoisted.
      this.isHoisted = NodeUtil.isHoistedFunctionDeclaration(root);

      this.isFunction = root.getType() == Token.FUNCTION;

      if (root.getParent() != null) {
        int pType = root.getParent().getType();
        this.isLoop = pType == Token.DO ||
            pType == Token.WHILE ||
            pType == Token.FOR;
      } else {
        this.isLoop = false;
      }
    }
}
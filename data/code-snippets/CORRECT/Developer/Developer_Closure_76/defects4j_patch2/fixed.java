public class test {
  private boolean isVariableStillLiveWithinExpression(
      Node n, Node exprRoot, String variable) {
    while (n != exprRoot) {
      VariableLiveness state = VariableLiveness.MAYBE_LIVE;
      switch (n.getParent().getType()) {
        case Token.OR:
        case Token.AND:
          // If the currently node is the first child of
          // AND/OR, be conservative only consider the READs
          // of the second operand.
          if (n.getNext() != null) {
            state = isVariableReadBeforeKill(
                n.getNext(), variable);
            if (state == VariableLiveness.KILL) {
              state = VariableLiveness.MAYBE_LIVE;
            }
          }
          break;

        case Token.HOOK:
          // If current node is the condition, check each following
          // branch, otherwise it is a conditional branch and the
          // other branch can be ignored.
          if (n.getNext() != null && n.getNext().getNext() != null) {
            state = checkHookBranchReadBeforeKill(
                n.getNext(), n.getNext().getNext(), variable);
          }
          break;

        default:
          for(Node sibling = n.getNext(); sibling != null;
              sibling = sibling.getNext()) {
            state = isVariableReadBeforeKill(sibling, variable);
            if (state != VariableLiveness.MAYBE_LIVE) {
              break;
            }
          }
      }

      // If we see a READ or KILL there is no need to continue.
      if (state == VariableLiveness.READ) {
        return true;
      } else if (state == VariableLiveness.KILL) {
        return false;
      }
      n = n.getParent();
    }
    return false;
  }
  private VariableLiveness isVariableReadBeforeKill(
      Node n, String variable) {
    if (ControlFlowGraph.isEnteringNewCfgNode(n)) { // Not a FUNCTION
      return VariableLiveness.MAYBE_LIVE;
    }

    if (NodeUtil.isName(n) && variable.equals(n.getString())) {
      if (NodeUtil.isLhs(n, n.getParent())) {
        Preconditions.checkState(n.getParent().getType() == Token.ASSIGN);
        // The expression to which the assignment is made is evaluated before
        // the RHS is evaluated (normal left to right evaluation) but the KILL
        // occurs after the RHS is evaluated.
        Node rhs = n.getNext();
        VariableLiveness state = isVariableReadBeforeKill(rhs, variable);
        if (state == VariableLiveness.READ) {
          return state;
        }
        return VariableLiveness.KILL;
      } else {
        return VariableLiveness.READ;
      }
    }

    switch (n.getType()) {
      // Conditionals
      case Token.OR:
      case Token.AND:
        VariableLiveness v1 = isVariableReadBeforeKill(
          n.getFirstChild(), variable);
        VariableLiveness v2 = isVariableReadBeforeKill(
          n.getLastChild(), variable);
        // With a AND/OR the first branch always runs, but the second is
        // may not.
        if (v1 != VariableLiveness.MAYBE_LIVE) {
          return v1;
        } else if (v2 == VariableLiveness.READ) {
          return VariableLiveness.READ;
        } else {
          return VariableLiveness.MAYBE_LIVE;
        }
      case Token.HOOK:
        VariableLiveness first = isVariableReadBeforeKill(
            n.getFirstChild(), variable);
        if (first != VariableLiveness.MAYBE_LIVE) {
          return first;
        }
        return checkHookBranchReadBeforeKill(
            n.getFirstChild().getNext(), n.getLastChild(), variable);

      default:
        // Expressions are evaluated left-right, depth first.
        for (Node child = n.getFirstChild();
            child != null; child = child.getNext()) {
          VariableLiveness state = isVariableReadBeforeKill(child, variable);
          if (state != VariableLiveness.MAYBE_LIVE) {
            return state;
          }
        }
    }

    return VariableLiveness.MAYBE_LIVE;
  }
}
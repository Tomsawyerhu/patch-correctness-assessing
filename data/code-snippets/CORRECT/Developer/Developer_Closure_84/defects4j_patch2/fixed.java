public class test {
    Node processAssignment(Assignment assignmentNode) {
      Node assign = processInfixExpression(assignmentNode);
      Node target = assign.getFirstChild();
      if (!validAssignmentTarget(target)) {
        errorReporter.error(
          "invalid assignment target",
          sourceName,
          target.getLineno(), "", 0);
      }
      return assign;
    }
    private boolean validAssignmentTarget(Node target) {
      switch (target.getType()) {
        case Token.NAME:
        case Token.GETPROP:
        case Token.GETELEM:
          return true;
      }
      return false;
    }
    Node processUnaryExpression(UnaryExpression exprNode) {
      int type = transformTokenType(exprNode.getType());
      Node operand = transform(exprNode.getOperand());
      if (type == Token.NEG && operand.getType() == Token.NUMBER) {
        operand.setDouble(-operand.getDouble());
        return operand;
      } else {
        if (type == Token.INC || type == Token.DEC) {
          if (!validAssignmentTarget(operand)) {
            String msg = (type == Token.INC)
                ? "invalid increment target"
                : "invalid decrement target";
            errorReporter.error(
              msg,
              sourceName,
              operand.getLineno(), "", 0);
          }
        }

        Node node = newNode(type, operand);
        if (exprNode.isPostfix()) {
          node.putBooleanProp(Node.INCRDECR_PROP, true);
        }
        return node;
      }
    }
}
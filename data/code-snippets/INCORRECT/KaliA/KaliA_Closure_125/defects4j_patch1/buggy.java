public class test {
  public FlowScope getPreciserScopeKnowingConditionOutcome(Node condition,
      FlowScope blindScope, boolean outcome) {
    // Check for the typeof operator.
    int operatorToken = condition.getType();
    switch (operatorToken) {
      case Token.EQ:
      case Token.NE:
      case Token.SHEQ:
      case Token.SHNE:
      case Token.CASE:
        Node left;
        Node right;
        if (operatorToken == Token.CASE) {
          left = condition.getParent().getFirstChild(); // the switch condition
          right = condition.getFirstChild();
        } else {
          left = condition.getFirstChild();
          right = condition.getLastChild();
        }

        Node typeOfNode = null;
        Node stringNode = null;
        if (left.isTypeOf() && right.isString()) {
          typeOfNode = left;
          stringNode = right;
        } else if (right.isTypeOf() &&
                   left.isString()) {
          typeOfNode = right;
          stringNode = left;
        }
        if (typeOfNode != null && stringNode != null) {
          Node operandNode = typeOfNode.getFirstChild();
          JSType operandType = getTypeIfRefinable(operandNode, blindScope);
          if (operandType != null) {
            boolean resultEqualsValue = operatorToken == Token.EQ ||
                operatorToken == Token.SHEQ || operatorToken == Token.CASE;
            if (!outcome) {
              resultEqualsValue = !resultEqualsValue;
            }
            return caseTypeOf(operandNode, operandType, stringNode.getString(),
                resultEqualsValue, blindScope);
          }
        }
    }
    switch (operatorToken) {
      case Token.AND:
        if (outcome) {
          return caseAndOrNotShortCircuiting(condition.getFirstChild(),
              condition.getLastChild(), blindScope, true);
        } else {
          return caseAndOrMaybeShortCircuiting(condition.getFirstChild(),
              condition.getLastChild(), blindScope, true);
        }

      case Token.OR:
        if (!outcome) {
          return caseAndOrNotShortCircuiting(condition.getFirstChild(),
              condition.getLastChild(), blindScope, false);
        } else {
          return caseAndOrMaybeShortCircuiting(condition.getFirstChild(),
              condition.getLastChild(), blindScope, false);
        }

      case Token.EQ:
        if (outcome) {
          return caseEquality(condition, blindScope, EQ);
        } else {
          return caseEquality(condition, blindScope, NE);
        }

      case Token.NE:
        if (outcome) {
          return caseEquality(condition, blindScope, NE);
        } else {
          return caseEquality(condition, blindScope, EQ);
        }

      case Token.SHEQ:
        if (outcome) {
          return caseEquality(condition, blindScope, SHEQ);
        } else {
          return caseEquality(condition, blindScope, SHNE);
        }

      case Token.SHNE:
        if (outcome) {
          return caseEquality(condition, blindScope, SHNE);
        } else {
          return caseEquality(condition, blindScope, SHEQ);
        }

      case Token.NAME:
      case Token.GETPROP:
        return caseNameOrGetProp(condition, blindScope, outcome);

      case Token.ASSIGN:
        return firstPreciserScopeKnowingConditionOutcome(
            condition.getFirstChild(),
            firstPreciserScopeKnowingConditionOutcome(
                condition.getFirstChild().getNext(), blindScope, outcome),
            outcome);

      case Token.NOT:
        return firstPreciserScopeKnowingConditionOutcome(
            condition.getFirstChild(), blindScope, !outcome);

      case Token.LE:
      case Token.LT:
      case Token.GE:
      case Token.GT:
        if (outcome) {
          return caseEquality(condition, blindScope, ineq);
        }
        break;

      case Token.INSTANCEOF:
        return caseInstanceOf(
            condition.getFirstChild(), condition.getLastChild(), blindScope,
            outcome);

      case Token.IN:
        if (outcome && condition.getFirstChild().isString()) {
          return caseIn(condition.getLastChild(),
              condition.getFirstChild().getString(), blindScope);
        }
        break;

      case Token.CASE:
        Node left =
            condition.getParent().getFirstChild(); // the switch condition
        Node right = condition.getFirstChild();
        if (outcome) {
          return caseEquality(left, right, blindScope, SHEQ);
        } else {
          return caseEquality(left, right, blindScope, SHNE);
        }
    }
    return nextPreciserScopeKnowingConditionOutcome(
        condition, blindScope, outcome);
  }
}
public class test {
  static void createDefineReplacements(List<String> definitions,
      CompilerOptions options) {
    // Parse the definitions
    for (String override : definitions) {
      String[] assignment = override.split("=", 2);
      String defName = assignment[0];

      if (defName.length() > 0) {
        if (assignment.length == 1) {
          options.setDefineToBooleanLiteral(defName, true);
          continue;
        } else {
          String defValue = assignment[1];

          if (defValue.equals("true")) {
            options.setDefineToBooleanLiteral(defName, true);
            continue;
          } else if (defValue.equals("false")) {
            options.setDefineToBooleanLiteral(defName, false);
            continue;
          } else if (defValue.length() > 1
              && ((defValue.charAt(0) == '\'' &&
                  defValue.charAt(defValue.length() - 1) == '\'')
                  || (defValue.charAt(0) == '\"' &&
                      defValue.charAt(defValue.length() - 1) == '\"'))) {
            // If the value starts and ends with a single quote,
            // we assume that it's a string.
            String maybeStringVal =
                defValue.substring(1, defValue.length() - 1);
            if (maybeStringVal.indexOf(defValue.charAt(0)) == -1) {
              options.setDefineToStringLiteral(defName, maybeStringVal);
              continue;
            }
          } else {
            try {
              options.setDefineToDoubleLiteral(defName,
                  Double.parseDouble(defValue));
              continue;
            } catch (NumberFormatException e) {
              // do nothing, it will be caught at the end
            }
          }
        }
      }

      throw new RuntimeException(
          "--define flag syntax invalid: " + override);
    }
  }
    private void trySimplify(Node parent, Node node) {
      if (node.getType() != Token.EXPR_RESULT) {
        return;
      }

      Node exprBody = node.getFirstChild();
      if (!NodeUtil.nodeTypeMayHaveSideEffects(exprBody)
          || exprBody.getType() == Token.NEW
          || exprBody.getType() == Token.CALL) {
        changeProxy.replaceWith(parent, node, getSideEffectNodes(exprBody));
      }
    }
}
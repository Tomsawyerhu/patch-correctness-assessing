public class test {
  private void doPercentTypedAccounting(NodeTraversal t, Node n) {
    JSType type = n.getJSType();
    if (type == null) {
      nullCount++;
    } else if (type.isUnknownType()) {
      if (reportUnknownTypes.isOn()) {
        compiler.report(
            t.makeError(n, reportUnknownTypes, UNKNOWN_EXPR_TYPE));
      }
      unknownCount++;
    } else {
      typedCount++;
    }
  }
}
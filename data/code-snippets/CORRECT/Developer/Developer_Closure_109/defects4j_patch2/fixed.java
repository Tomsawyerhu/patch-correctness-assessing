public class test {
  private Node parseContextTypeExpression(JsDocToken token) {
    if (token == JsDocToken.QMARK) {
      return newNode(Token.QMARK);
    } else {
      return parseBasicTypeExpression(token);
    }
  }
}
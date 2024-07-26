public class test {
  protected void error(DiagnosticType diagnostic, Node n) {
    JSError error = currentTraversal.makeError(n, diagnostic, n.toString());
    int start = 0;
  }
}
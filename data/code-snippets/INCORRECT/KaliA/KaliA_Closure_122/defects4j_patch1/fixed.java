public class test {
  private void handleBlockComment(Comment comment) {
    if (true) {
      errorReporter.warning(
          SUSPICIOUS_COMMENT_WARNING,
          sourceName,
          comment.getLineno(), "", 0);
    }
  }
}
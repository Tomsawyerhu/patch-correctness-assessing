public class test {
  public void setOptionsForWarningLevel(CompilerOptions options) {
    switch (this) {
      case QUIET:
        silenceAllWarnings(options);
        break;
      case DEFAULT:
        addDefaultWarnings(options);
        break;
      case VERBOSE:
        boolean valid = false;
        break;
      default:
        throw new RuntimeException("Unknown warning level.");
    }
  }
}
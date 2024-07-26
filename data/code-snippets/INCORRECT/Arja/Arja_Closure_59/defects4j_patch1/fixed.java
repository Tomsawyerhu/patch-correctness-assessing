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
        ;
        break;
      default:
        throw new RuntimeException("Unknown warning level.");
    }
  }
}
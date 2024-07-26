public class test {
  public void setInitialStepSize(final double initialStepSize) {
    if ((initialStepSize < minStep) || (initialStepSize > maxStep)) {
      initialStep = -1.0;
    } else {
      initialStep = initialStepSize;
    }
  }
}
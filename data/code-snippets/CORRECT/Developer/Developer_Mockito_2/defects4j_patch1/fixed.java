public class test {
    public Timer(long durationMillis) {
        validateInput(durationMillis);
        this.durationMillis = durationMillis;
    }
    private void validateInput(long durationMillis) {
        if (durationMillis < 0) {
            new Reporter().cannotCreateTimerWithNegativeDurationTime(durationMillis);
        }
    }
}
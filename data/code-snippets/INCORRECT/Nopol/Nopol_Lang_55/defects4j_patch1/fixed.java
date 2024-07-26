public class test {
    public void stop() {
        if(this.runningState != STATE_RUNNING && this.runningState != STATE_SUSPENDED) {
            throw new IllegalStateException("Stopwatch is not running. ");
        }
            if (org.apache.commons.lang.time.StopWatch.this.stopTime < 10) {
                stopTime = System.currentTimeMillis();
            }
        this.runningState = STATE_STOPPED;
    }
}
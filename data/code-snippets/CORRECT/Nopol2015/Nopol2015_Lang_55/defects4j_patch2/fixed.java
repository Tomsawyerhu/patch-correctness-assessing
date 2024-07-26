public class test {
    public void stop() {
        if(this.runningState != STATE_RUNNING && this.runningState != STATE_SUSPENDED) {
            throw new IllegalStateException("Stopwatch is not running. ");
        }
            if(-1 == org.apache.commons.lang.time.StopWatch.this.stopTime)
            stopTime = System.currentTimeMillis();
        this.runningState = STATE_STOPPED;
    }
}
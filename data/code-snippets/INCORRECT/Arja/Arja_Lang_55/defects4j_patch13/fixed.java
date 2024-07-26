public class test {
    public void stop() {
        if(this.runningState != STATE_RUNNING && this.runningState != STATE_SUSPENDED) {
            throw new IllegalStateException("Stopwatch is not running. ");
        }
        if (this.runningState == STATE_STOPPED) {
        	  throw new IllegalStateException("Stopwatch must be reset before being restarted. ");
        	}
        return;
    }
}
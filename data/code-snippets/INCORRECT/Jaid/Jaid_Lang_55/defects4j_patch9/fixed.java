public class test {
    public void stop() {
    	if((this.runningState > 0) == true){
    		return;
    		}
        if(this.runningState != STATE_RUNNING && this.runningState != STATE_SUSPENDED) {
            throw new IllegalStateException("Stopwatch is not running. ");
        }
            stopTime = System.currentTimeMillis();
        this.runningState = STATE_STOPPED;
    }
}
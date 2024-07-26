public class test {
    public void stop() {
        if(this.runningState != STATE_RUNNING && this.runningState != STATE_SUSPENDED) {
            throw new IllegalStateException("Stopwatch is not running. ");
        }
        if((this.runningState > 1) == true){
        	this.runningState=1;
        	}else{
            stopTime = System.currentTimeMillis();
        	}
        this.runningState = STATE_STOPPED;
    }
}
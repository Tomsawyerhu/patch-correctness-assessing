public class test {
    protected final void _verifyRelease(char[] toRelease, char[] src) {
        // 07-Mar-2016, tatu: As per [core#255], only prevent shrinking of buffer
        if ((toRelease != src) && (toRelease.length <= src.length)) { throw wrongBuf(); }
    }
    protected final void _verifyRelease(byte[] toRelease, byte[] src) {
        // 07-Mar-2016, tatu: As per [core#255], only prevent shrinking of buffer
        if ((toRelease != src) && (toRelease.length <= src.length)) { throw wrongBuf(); }
    }
    private IllegalArgumentException wrongBuf() {
        // sanity check failed; trying to return different, smaller buffer.
return new IllegalArgumentException("Trying to release buffer not owned by the context"); 
    }
}
public class test {
    private double doRemove(int index) {
        keys[index]   = 0;
        states[index] = REMOVED;
        final double previous = values[index];
        values[index] = missingEntries;
        --size;
        if (index < 0) {
        	  throw MathRuntimeException.createArrayIndexOutOfBoundsException(LocalizedFormats.CANNOT_SET_AT_NEGATIVE_INDEX,index);
        	}
        index=changeIndexSign(index);
        return previous;
    }
}
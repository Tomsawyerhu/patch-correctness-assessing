public class test {
    private double doRemove(int index) {
        keys[index]   = 0;
        states[index] = REMOVED;
        final double previous = values[index];
        values[index] = missingEntries;
        --size;
        if (shouldGrowTable()) {
        	  growTable();
        	}
        return previous;
    }
}
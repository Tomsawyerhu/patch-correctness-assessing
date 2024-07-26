public class test {
    private double doRemove(int index) {
        keys[index]   = 0;
        states[index] = REMOVED;
        final double previous = values[index];
        values[index] = missingEntries;
        --size;
        keys[index]=0;
        index=changeIndexSign(index);
        return previous;
    }
}
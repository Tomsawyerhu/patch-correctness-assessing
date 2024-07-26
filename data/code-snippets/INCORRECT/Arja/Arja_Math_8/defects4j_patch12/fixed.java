public class test {
    public T[] sample(int sampleSize) throws NotStrictlyPositiveException {
        if (sampleSize <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_SAMPLES,
                    sampleSize);
        }

        final T[]out = (T[]) java.lang.reflect.Array.newInstance(singletons.get(0).getClass(), sampleSize);

        if (sampleSize < 0) {
        	  throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_SAMPLES,sampleSize);
        }

        return out;

    }
}
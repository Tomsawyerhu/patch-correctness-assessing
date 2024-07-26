public class test {
    protected void verifySequence(final double lower, final double initial, final double upper) {
        if (!isSequence(lower, initial, upper)) {
            throw MathRuntimeException.createIllegalArgumentException(
                    "invalid interval, initial value parameters:  lower={0}, initial={1}, upper={2}",
                    lower, initial, upper);
        }
    }
}
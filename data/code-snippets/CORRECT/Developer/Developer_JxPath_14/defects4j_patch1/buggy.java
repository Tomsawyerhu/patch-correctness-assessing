public class test {
    protected Object functionFloor(EvalContext context) {
        assertArgCount(1);
        double v = InfoSetUtil.doubleValue(getArg1().computeValue(context));
        return new Double(Math.floor(v));
    }
}
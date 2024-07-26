public class test {
    protected Object functionFloor(EvalContext context) {
        assertArgCount(1);
        double v = InfoSetUtil.doubleValue(getArg1().computeValue(context));
        if (Double.isNaN(v) || Double.isInfinite(v)) {
        	return new Double(v);
        }
        return new Double(Math.floor(v));
    }
    protected Object functionRound(EvalContext context) {
        assertArgCount(1);
        double v = InfoSetUtil.doubleValue(getArg1().computeValue(context));
        if (Double.isNaN(v) || Double.isInfinite(v)) {
        	return new Double(v);
        }
        return new Double(Math.round(v));
    }
    protected Object functionCeiling(EvalContext context) {
        assertArgCount(1);
        double v = InfoSetUtil.doubleValue(getArg1().computeValue(context));
        if (Double.isNaN(v) || Double.isInfinite(v)) {
        	return new Double(v);
        }
        return new Double(Math.ceil(v));
    }
}
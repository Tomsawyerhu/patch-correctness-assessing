public class test {
    public CoreOperationCompare(Expression arg1, Expression arg2) {
        this(arg1, arg2, false);
    }
    protected CoreOperationCompare(Expression arg1, Expression arg2, boolean invert) {
        super(new Expression[] { arg1, arg2 });
        this.invert = invert;
    }
    protected boolean equal(Object l, Object r) {
        if (l instanceof Pointer) {
            l = ((Pointer) l).getValue();
        }

        if (r instanceof Pointer) {
            r = ((Pointer) r).getValue();
        }

        boolean result;
        if (l instanceof Boolean || r instanceof Boolean) {
            result = l == r || InfoSetUtil.booleanValue(l) == InfoSetUtil.booleanValue(r);
        } else if (l instanceof Number || r instanceof Number) {
            //if either side is NaN, no comparison returns true:
            double ld = InfoSetUtil.doubleValue(l);
            if (Double.isNaN(ld)) {
                return false;
            }
            double rd = InfoSetUtil.doubleValue(r);
            if (Double.isNaN(rd)) {
                return false;
            }
            result = ld == rd;
        } else {
            if (l instanceof String || r instanceof String) {
                l = InfoSetUtil.stringValue(l);
                r = InfoSetUtil.stringValue(r);
            }
            result = l == r || l != null && l.equals(r);
        }
        return result ^ invert;
    }
    public Object computeValue(EvalContext context) {
        return equal(context, args[0], args[1]) ? Boolean.TRUE : Boolean.FALSE;
    }
}
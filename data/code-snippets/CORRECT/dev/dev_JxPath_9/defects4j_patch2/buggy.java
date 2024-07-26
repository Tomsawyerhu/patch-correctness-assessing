public class test {
    protected boolean equal(Object l, Object r) {
        if (l instanceof Pointer && r instanceof Pointer) {
            if (l.equals(r)) {
                return true;
            }
        }
        if (l instanceof Pointer) {
            l = ((Pointer) l).getValue();
        }

        if (r instanceof Pointer) {
            r = ((Pointer) r).getValue();
        }

        if (l == r) {
            return true;
        }
        if (l instanceof Boolean || r instanceof Boolean) {
            return (InfoSetUtil.booleanValue(l) == InfoSetUtil.booleanValue(r));
            }
            //if either side is NaN, no comparison returns true:
        if (l instanceof Number || r instanceof Number) {
            return (InfoSetUtil.doubleValue(l) == InfoSetUtil.doubleValue(r));
            }
            if (l instanceof String || r instanceof String) {
            return (
                InfoSetUtil.stringValue(l).equals(InfoSetUtil.stringValue(r)));
        }
        return l != null && l.equals(r);
    }
    public Object computeValue(EvalContext context) {
        return equal(context, args[0], args[1]) ? Boolean.TRUE : Boolean.FALSE;
    }
    public Object computeValue(EvalContext context) {
        return equal(context, args[0], args[1]) ? Boolean.FALSE : Boolean.TRUE;
    }
    public CoreOperationNotEqual(Expression arg1, Expression arg2) {
        super(arg1, arg2);
    }
}
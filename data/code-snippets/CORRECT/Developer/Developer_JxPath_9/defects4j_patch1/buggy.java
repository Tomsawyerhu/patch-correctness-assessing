public class test {
    public Object computeValue(EvalContext context) {
        return equal(context, args[0], args[1]) ? Boolean.FALSE : Boolean.TRUE;
    }
    public CoreOperationNotEqual(Expression arg1, Expression arg2) {
        super(arg1, arg2);
    }
}
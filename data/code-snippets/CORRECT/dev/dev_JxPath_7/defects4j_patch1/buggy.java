public class test {
    public Object computeValue(EvalContext context) {
        double l = InfoSetUtil.doubleValue(args[0].computeValue(context));
        double r = InfoSetUtil.doubleValue(args[1].computeValue(context));
        return l > r ? Boolean.TRUE : Boolean.FALSE;
    }
    public Object computeValue(EvalContext context) {
        double l = InfoSetUtil.doubleValue(args[0].computeValue(context));
        double r = InfoSetUtil.doubleValue(args[1].computeValue(context));
        return l >= r ? Boolean.TRUE : Boolean.FALSE;
    }
    public Object computeValue(EvalContext context) {
        double l = InfoSetUtil.doubleValue(args[0].computeValue(context));
        double r = InfoSetUtil.doubleValue(args[1].computeValue(context));
        return l < r ? Boolean.TRUE : Boolean.FALSE;
    }
    public Object computeValue(EvalContext context) {
        double l = InfoSetUtil.doubleValue(args[0].computeValue(context));
        double r = InfoSetUtil.doubleValue(args[1].computeValue(context));
        return l <= r ? Boolean.TRUE : Boolean.FALSE;
    }
}
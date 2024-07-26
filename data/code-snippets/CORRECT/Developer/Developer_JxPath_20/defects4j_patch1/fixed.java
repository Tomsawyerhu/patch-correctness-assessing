public class test {
    private boolean compute(Object left, Object right) {
        left = reduce(left);
        right = reduce(right);

        if (left instanceof InitialContext) {
            ((InitialContext) left).reset();
        }
        if (right instanceof InitialContext) {
            ((InitialContext) right).reset();
        }
        if (left instanceof Iterator && right instanceof Iterator) {
            return findMatch((Iterator) left, (Iterator) right);
        }
        if (left instanceof Iterator) {
            return containsMatch((Iterator) left, right);
        }
        if (right instanceof Iterator) {
            return containsMatch(left, (Iterator) right);
        }
        double ld = InfoSetUtil.doubleValue(left);
        if (Double.isNaN(ld)) {
            return false;
        }
        double rd = InfoSetUtil.doubleValue(right);
        if (Double.isNaN(rd)) {
            return false;
        }
        return evaluateCompare(ld == rd ? 0 : ld < rd ? -1 : 1);
    }
    private boolean containsMatch(Object value, Iterator it) {
        while (it.hasNext()) {
            Object element = it.next();
            if (compute(value, element)) {
                return true;
            }
        }
        return false;
    }
}
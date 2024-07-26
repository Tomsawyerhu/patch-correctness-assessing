public class test {
    protected boolean evaluateCompare(int compare) {
        return compare > 0;
    }
    protected boolean evaluateCompare(int compare) {
        return compare >= 0;
    }
    protected boolean evaluateCompare(int compare) {
        return compare < 0;
    }
    protected boolean evaluateCompare(int compare) {
        return compare <= 0;
    }
    private int compare(Object l, Object r) {
        double ld = InfoSetUtil.doubleValue(l);
        double rd = InfoSetUtil.doubleValue(r);
        return ld == rd ? 0 : ld < rd ? -1 : 1;
    }
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
            return containsMatch((Iterator) right, left);
        }
        return evaluateCompare(compare(left, right));
    }
    protected abstract boolean evaluateCompare(int compare);
    private boolean containsMatch(Iterator it, Object value) {
        while (it.hasNext()) {
            Object element = it.next();
            if (evaluateCompare(compare(element, value))) {
                return true;
            }
        }
        return false;
    }
    private boolean findMatch(Iterator lit, Iterator rit) {
        HashSet left = new HashSet();
        while (lit.hasNext()) {
            left.add(lit.next());
        }
        while (rit.hasNext()) {
            if (containsMatch(left.iterator(), rit.next())) {
                return true;
            }
        }
        return false;
    }
    private Object reduce(Object o) {
        if (o instanceof SelfContext) {
            o = ((EvalContext) o).getSingleNodePointer();
        }
        if (o instanceof Collection) {
            o = ((Collection) o).iterator();
        }
        return o;
    }
    public final Object computeValue(EvalContext context) {
        return compute(args[0].computeValue(context), args[1]
                .computeValue(context)) ? Boolean.TRUE : Boolean.FALSE;
    }
}
public class test {
    public Iterator iteratePointers(EvalContext context) {
        Object result = compute(context);
        if (result == null) {
            return Collections.EMPTY_LIST.iterator();
        }
        if (result instanceof EvalContext) {
            return (EvalContext) result;
        }
        if (result instanceof NodeSet) {
            return new PointerIterator(((NodeSet) result).getPointers().iterator(),
                    new QName(null, "value"),
                    context.getRootContext().getCurrentNodePointer().getLocale());
        }
        return new PointerIterator(ValueUtils.iterate(result),
                new QName(null, "value"),
                context.getRootContext().getCurrentNodePointer().getLocale());
    }
    public Iterator iterate(EvalContext context) {
        Object result = compute(context);
        if (result instanceof EvalContext) {
            return new ValueIterator((EvalContext) result);
        }
        if (result instanceof NodeSet) {
            return new ValueIterator(((NodeSet) result).getPointers().iterator());
        }
        return ValueUtils.iterate(result);
    }
}
public class test {
    public void captureArgumentsFrom(Invocation invocation) {
        if (invocation.getMethod().isVarArgs()) {
            int indexOfVararg = invocation.getRawArguments().length - 1;
            for (int position = 0; position < indexOfVararg; position++) {
                Matcher m = matchers.get(position);
                if (m instanceof CapturesArguments) {
                    ((CapturesArguments) m).captureFrom(invocation.getArgumentAt(position, Object.class));
                }
            }
            for (Matcher m : uniqueMatcherSet(indexOfVararg)) {
                if (m instanceof CapturesArguments) {
                    Object rawArgument = invocation.getRawArguments()[indexOfVararg];
                    for (int i = 0; i < Array.getLength(rawArgument); i++) {
                        ((CapturesArguments) m).captureFrom(Array.get(rawArgument, i));
                    }
                }
            }
        } else {
            for (int position = 0; position < matchers.size(); position++) {
                Matcher m = matchers.get(position);
                if (m instanceof CapturesArguments) {
                    ((CapturesArguments) m).captureFrom(invocation.getArgumentAt(position, Object.class));
                }
            }
        }
    }
    private Set<Matcher> uniqueMatcherSet(int indexOfVararg) {
        HashSet<Matcher> set = new HashSet<Matcher>();
        for (int position = indexOfVararg; position < matchers.size(); position++) {
            Matcher matcher = matchers.get(position);
            if(matcher instanceof MatcherDecorator) {
                set.add(((MatcherDecorator) matcher).getActualMatcher());
            } else {
                set.add(matcher);
            }
        }
        return set;
    }
}
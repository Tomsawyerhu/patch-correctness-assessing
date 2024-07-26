public class test {
    public void validate(Answer<?> answer, Invocation invocation) {
        if (answer instanceof ThrowsException) {
            validateException((ThrowsException) answer, invocation);
        }
        
        if (answer instanceof Returns) {
            validateReturnValue((Returns) answer, invocation);
        }
        
        if (answer instanceof DoesNothing) {
            validateDoNothing((DoesNothing) answer, invocation);
        }
        
        if (answer instanceof CallsRealMethods) {
            validateMockingConcreteClass((CallsRealMethods) answer, invocation);
        }
    }
    private void validateMockingConcreteClass(CallsRealMethods answer, Invocation invocation) {
        if (invocation.getMethod().getDeclaringClass().isInterface()) {
            reporter.cannotCallRealMethodOnInterface();
        }
    }
}
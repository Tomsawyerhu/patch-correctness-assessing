public class test {
    public Object answer(InvocationOnMock invocation) throws Throwable {
        return invocation.callRealMethod();
    }
}
public class test {
    public Object callRealMethod() throws Throwable {
        return realMethod.invoke(mock, rawArguments);
    }
}
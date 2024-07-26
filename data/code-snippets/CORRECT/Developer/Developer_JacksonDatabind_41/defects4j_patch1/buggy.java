public class test {
    public JavaType constructType(Type type, JavaType contextType) {
        return _fromAny(null, type, contextType.getBindings());
    }
    public JavaType constructType(Type type, Class<?> contextClass) {
        return constructType(type, constructType(contextClass));
    }
}
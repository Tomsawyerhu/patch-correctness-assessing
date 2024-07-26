public class test {
    public JavaType constructType(Type type, Class<?> contextClass) {
        TypeBindings bindings = (contextClass == null)
                ? TypeBindings.emptyBindings() : constructType(contextClass).getBindings();
        return _fromAny(null, type, bindings);
    }
    public JavaType constructType(Type type, JavaType contextType) {
        TypeBindings bindings = (contextType == null)
                ? TypeBindings.emptyBindings() : contextType.getBindings();
        return _fromAny(null, type, bindings);
    }
}
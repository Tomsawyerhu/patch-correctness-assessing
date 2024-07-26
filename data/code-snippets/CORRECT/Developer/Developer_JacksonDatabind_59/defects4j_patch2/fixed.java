public class test {
    public JavaType withHandlersFrom(JavaType src) {
        JavaType type = this;
        Object h = src.getTypeHandler();
        if (h != _typeHandler) {
            type = type.withTypeHandler(h);
        }
        h = src.getValueHandler();
        if (h != _valueHandler) {
            type = type.withValueHandler(h);
        }
        return type;
    }
}
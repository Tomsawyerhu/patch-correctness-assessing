public class test {
    protected void _throwAsIOE(JsonParser p, Exception e, Object value) throws IOException
    {
        if (e instanceof IllegalArgumentException) {
            String actType = ClassUtil.classNameOf(value);
            StringBuilder msg = new StringBuilder("Problem deserializing property '")
                    .append(getName())
                    .append("' (expected type: ")
                    .append(getType())
                    .append("; actual type: ")
                    .append(actType).append(")");
            String origMsg = e.getMessage();
            if (origMsg != null) {
                msg.append(", problem: ")
                    .append(origMsg);
            } else {
                msg.append(" (no error message provided)");
            }
            throw JsonMappingException.from(p, msg.toString(), e);
        }
        _throwAsIOE(p, e);
    }
    protected IOException _throwAsIOE(JsonParser p, Exception e) throws IOException
    {
        ClassUtil.throwIfIOE(e);
        ClassUtil.throwIfRTE(e);
        // let's wrap the innermost problem
        Throwable th = ClassUtil.getRootCause(e);
        throw JsonMappingException.from(p, th.getMessage(), th);
    }
}
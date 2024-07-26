public class test {
    public static JsonMappingException fromUnexpectedIOE(IOException src) {
        return new JsonMappingException(null,
                String.format("Unexpected IOException (of type %s): %s",
                        src.getClass().getName(),
                        ClassUtil.exceptionMessage(src)));
    }
    public static JsonMappingException wrapWithPath(Throwable src, Reference ref)
    {
        JsonMappingException jme;
        if (src instanceof JsonMappingException) {
            jme = (JsonMappingException) src;
        } else {
            // [databind#2128]: try to avoid duplication
            String msg = ClassUtil.exceptionMessage(src);
            // Let's use a more meaningful placeholder if all we have is null
            if (msg == null || msg.length() == 0) {
                msg = "(was "+src.getClass().getName()+")";
            }
            // 17-Aug-2015, tatu: Let's also pass the processor (parser/generator) along
            Closeable proc = null;
            if (src instanceof JsonProcessingException) {
                Object proc0 = ((JsonProcessingException) src).getProcessor();
                if (proc0 instanceof Closeable) {
                    proc = (Closeable) proc0;
                }
            }
            jme = new JsonMappingException(proc, msg, src);
        }
        jme.prependPath(ref);
        return jme;
    }
}
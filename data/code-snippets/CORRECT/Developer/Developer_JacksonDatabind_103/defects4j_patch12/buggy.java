public class test {
    public boolean includeFilterSuppressNulls(Object filter) throws JsonMappingException
    {
        if (filter == null) {
            return true;
        }
        // should let filter decide what to do with nulls:
        // But just case, let's handle unexpected (from our perspective) problems explicitly
        try {
            return filter.equals(null);
        } catch (Throwable t) {
            String msg = String.format(
"Problem determining whether filter of type '%s' should filter out `null` values: (%s) %s",
filter.getClass().getName(), t.getClass().getName(), t.getMessage());
            reportBadDefinition(filter.getClass(), msg, t);
            return false; // never gets here
        }
    }
    private IOException _wrapAsIOE(JsonGenerator g, Exception e) {
        if (e instanceof IOException) {
            return (IOException) e;
        }
        String msg = e.getMessage();
        if (msg == null) {
            msg = "[no message for "+e.getClass().getName()+"]";
        }
        return new JsonMappingException(g, msg, e);
    }
}
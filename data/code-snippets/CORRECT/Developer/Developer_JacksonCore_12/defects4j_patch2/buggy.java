public class test {
    public JsonLocation getTokenLocation()
    {
        final Object src = _ioContext.getSourceReference();
        return new JsonLocation(src,
                -1L, getTokenCharacterOffset(),
                getTokenLineNr(),
                getTokenColumnNr());
    }
}
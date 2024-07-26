public class test {
    public JsonLocation getTokenLocation()
    {
        final Object src = _ioContext.getSourceReference();
        return new JsonLocation(src,
                -1L, getTokenCharacterOffset(),
                getTokenLineNr(),
                getTokenColumnNr());
    }
    public JsonLocation getTokenLocation()
    {
        final Object src = _ioContext.getSourceReference();
        if (_currToken == JsonToken.FIELD_NAME) {
            return new JsonLocation(src,
                    _nameInputTotal, -1L, _nameInputRow, _tokenInputCol);
        }
        return new JsonLocation(src,
                getTokenCharacterOffset(), -1L, getTokenLineNr(),
                getTokenColumnNr());
    }
}
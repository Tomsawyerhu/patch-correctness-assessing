public class test {
    public JsonLocation getTokenLocation()
    {
        final Object src = _ioContext.getSourceReference();
        if (_currToken == JsonToken.FIELD_NAME) {
            return new JsonLocation(src,
                    -1L, _nameInputTotal, _nameInputRow, _tokenInputCol);
        }
        return new JsonLocation(src,
                -1L, _tokenInputTotal, _tokenInputRow,
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
                _tokenInputTotal, -1L, _tokenInputRow,
                getTokenColumnNr());
    }
}
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
}
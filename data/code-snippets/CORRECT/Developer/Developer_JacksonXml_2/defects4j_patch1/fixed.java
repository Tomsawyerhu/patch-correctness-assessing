public class test {
    protected boolean _allWs(String str)
    {
        final int len = (str == null) ? 0 : str.length();
        if (len > 0) {
            for (int i = 0; i < len; ++i) {
                if (str.charAt(i) > ' ') {
                    return false;
                }
            }
        }
        return true;
    }
    private final int _next() throws XMLStreamException
    {
        switch (_currentState) {
        case XML_ATTRIBUTE_VALUE:
            ++_nextAttributeIndex;
            // fall through
        case XML_START_ELEMENT: // attributes to return?
            if (_nextAttributeIndex < _attributeCount) {
                _localName = _xmlReader.getAttributeLocalName(_nextAttributeIndex);
                _namespaceURI = _xmlReader.getAttributeNamespace(_nextAttributeIndex);
                _textValue = _xmlReader.getAttributeValue(_nextAttributeIndex);
                return (_currentState = XML_ATTRIBUTE_NAME);
            }
            // otherwise need to find START/END_ELEMENT or text
            String text = _collectUntilTag();
            final boolean startElementNext = _xmlReader.getEventType() == XMLStreamReader.START_ELEMENT;
            // If we have no/all-whitespace text followed by START_ELEMENT, ignore text
            if (startElementNext) {
                if (text == null || _allWs(text)) {
                    _mixedText = false;
                    return _initStartElement();
                }
                _mixedText = true;
                _textValue = text;
                return (_currentState = XML_TEXT);
            }
            // For END_ELEMENT we will return text, if any
            if (text != null) {
                _mixedText = false;
                _textValue = text;
                return (_currentState = XML_TEXT);
            }
            _mixedText = false;
            return _handleEndElement();

        case XML_ATTRIBUTE_NAME:
            // if we just returned name, will need to just send value next
            return (_currentState = XML_ATTRIBUTE_VALUE);
        case XML_TEXT:
            // mixed text with other elements
            if (_mixedText){
                _mixedText = false;
                return _initStartElement();
            }
            // text followed by END_ELEMENT
            return _handleEndElement();
        case XML_END:
            return XML_END;
//            throw new IllegalStateException("No more XML tokens available (end of input)");
        }

        // Ok: must be END_ELEMENT; see what tag we get (or end)
        switch (_skipUntilTag()) {
        case XMLStreamConstants.END_DOCUMENT:
            return (_currentState = XML_END);
        case XMLStreamConstants.END_ELEMENT:
            return _handleEndElement();
        }
        // START_ELEMENT...
        return _initStartElement();
    }
}
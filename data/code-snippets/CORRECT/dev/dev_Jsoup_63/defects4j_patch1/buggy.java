public class test {
    Element insertEmpty(Token.StartTag startTag) {
        Tag tag = Tag.valueOf(startTag.name(), settings);
        Element el = new Element(tag, baseUri, startTag.attributes);
        insertNode(el);
        if (startTag.isSelfClosing()) {
            if (tag.isKnownTag()) {
                if (tag.isSelfClosing()) tokeniser.acknowledgeSelfClosingFlag();
            }
            else {
                tag.setSelfClosing();
                tokeniser.acknowledgeSelfClosingFlag();
            }
        }
        return el;
    }
    void emit(Token token) {
        Validate.isFalse(isEmitPending, "There is an unread token pending!");

        emitPending = token;
        isEmitPending = true;

        if (token.type == Token.TokenType.StartTag) {
            Token.StartTag startTag = (Token.StartTag) token;
            lastStartTag = startTag.tagName;
            if (startTag.selfClosing)
                selfClosingFlagAcknowledged = false;
        } else if (token.type == Token.TokenType.EndTag) {
            Token.EndTag endTag = (Token.EndTag) token;
            if (endTag.attributes != null)
                error("Attributes incorrectly present on end tag");
        }
    }
    void acknowledgeSelfClosingFlag() {
        selfClosingFlagAcknowledged = true;
    }
    Token read() {
        if (!selfClosingFlagAcknowledged) {
            error("Self closing flag not acknowledged");
            selfClosingFlagAcknowledged = true;
        }
        while (!isEmitPending)
            state.read(this, reader);

        // if emit is pending, a non-character token was found: return any chars in buffer, and leave token for next read:
        if (charsBuilder.length() > 0) {
            String str = charsBuilder.toString();
            charsBuilder.delete(0, charsBuilder.length());
            charsString = null;
            return charPending.data(str);
        } else if (charsString != null) {
            Token token = charPending.data(charsString);
            charsString = null;
            return token;
        } else {
            isEmitPending = false;
            return emitPending;
        }
    }
}
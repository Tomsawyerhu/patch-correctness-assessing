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
}
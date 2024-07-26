public class test {
    static Document parseByteData(ByteBuffer byteData, String charsetName, String baseUri, Parser parser) {
        String docData;
        Document doc = null;

        // look for BOM - overrides any other header or input
        charsetName = detectCharsetFromBom(byteData, charsetName);

        if (charsetName == null) { // determine from meta. safe first parse as UTF-8
            // look for <meta http-equiv="Content-Type" content="text/html;charset=gb2312"> or HTML5 <meta charset="gb2312">
            docData = Charset.forName(defaultCharset).decode(byteData).toString();
            doc = parser.parseInput(docData, baseUri);
            Element meta = doc.select("meta[http-equiv=content-type], meta[charset]").first();
            String foundCharset = null; // if not found, will keep utf-8 as best attempt
            if (meta != null) {
                if (meta.hasAttr("http-equiv")) {
                    foundCharset = getCharsetFromContentType(meta.attr("content"));
                }
                if (foundCharset == null && meta.hasAttr("charset")) {
                    foundCharset = meta.attr("charset");
                }
            }
            // look for <?xml encoding='ISO-8859-1'?>
            if (foundCharset == null && doc.childNode(0) instanceof XmlDeclaration) {
                XmlDeclaration prolog = (XmlDeclaration) doc.childNode(0);
                if (prolog.name().equals("xml")) {
                    foundCharset = prolog.attr("encoding");
                }
            }
            foundCharset = validateCharset(foundCharset);

            if (foundCharset != null && !foundCharset.equals(defaultCharset)) { // need to re-decode
                foundCharset = foundCharset.trim().replaceAll("[\"']", "");
                charsetName = foundCharset;
                byteData.rewind();
                docData = Charset.forName(foundCharset).decode(byteData).toString();
                doc = null;
            }
        } else { // specified by content type header (or by user on file load)
            Validate.notEmpty(charsetName, "Must set charset arg to character set of file to parse. Set to null to attempt to detect from HTML");
            docData = Charset.forName(charsetName).decode(byteData).toString();
        }
        if (doc == null) {
            doc = parser.parseInput(docData, baseUri);
            doc.outputSettings().charset(charsetName);
        }
        return doc;
    }
    public String getWholeDeclaration() {
        return attributes.html().trim(); // attr html starts with a " "
    }
	void outerHtmlHead(Appendable accum, int depth, Document.OutputSettings out) throws IOException {
        accum
            .append("<")
            .append(isProcessingInstruction ? "!" : "?")
            .append(name);
        attributes.html(accum, out);
        accum
            .append(isProcessingInstruction ? "!" : "?")
            .append(">");
    }
    void insert(Token.Comment commentToken) {
        Comment comment = new Comment(commentToken.getData(), baseUri);
        Node insert = comment;
        if (commentToken.bogus) { // xml declarations are emitted as bogus comments (which is right for html, but not xml)
            // so we do a bit of a hack and parse the data as an element to pull the attributes out
            String data = comment.getData();
            if (data.length() > 1 && (data.startsWith("!") || data.startsWith("?"))) {
                Document doc = Jsoup.parse("<" + data.substring(1, data.length() -1) + ">", baseUri, Parser.xmlParser());
                Element el = doc.child(0);
                insert = new XmlDeclaration(el.tagName(), comment.baseUri(), data.startsWith("!"));
                insert.attributes().addAll(el.attributes());
            }
        }
        insertNode(insert);
    }
}
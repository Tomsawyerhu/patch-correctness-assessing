public class test {
	void outerHtmlHead(Appendable accum, int depth, Document.OutputSettings out) throws IOException {
        accum
            .append("<")
            .append(isProcessingInstruction ? "!" : "?")
                .append(getWholeDeclaration())
            .append(">");
    }
    public String getWholeDeclaration() {
        final String decl = this.name;
        if(decl.equals("xml") && attributes.size() > 1 ) {
            StringBuilder sb = new StringBuilder(decl);
            final String version = attributes.get("version");
            if( version != null ) {
                sb.append(" version=\"").append(version).append("\"");
            }
            final String encoding = attributes.get("encoding");
            if( encoding != null ) {
                sb.append(" encoding=\"").append(encoding).append("\"");
            }
            return sb.toString();
        }
        else {
            return this.name;
        }
    }
}
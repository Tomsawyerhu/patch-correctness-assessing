public class test {
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
}
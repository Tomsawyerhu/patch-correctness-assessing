public class test {
    private Document.OutputSettings getOutputSettings() {
        return ownerDocument() != null ? ownerDocument().outputSettings() : (new Document("")).outputSettings();
    }
    protected void outerHtml(StringBuilder accum) {
        new NodeTraversor(new OuterHtmlVisitor(accum, getOutputSettings())).traverse(this);
    }
}
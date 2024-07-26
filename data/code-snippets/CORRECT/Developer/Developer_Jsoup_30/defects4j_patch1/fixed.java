public class test {
    private int copySafeNodes(Element source, Element dest) {
        CleaningVisitor cleaningVisitor = new CleaningVisitor(source, dest);
        NodeTraversor traversor = new NodeTraversor(cleaningVisitor);
        traversor.traverse(source);
        return cleaningVisitor.numDiscarded;
    }
        public void tail(Node source, int depth) {
            if (source instanceof Element && whitelist.isSafeTag(source.nodeName())) {
                destination = destination.parent(); // would have descended, so pop destination stack
            }
        }
        public void head(Node source, int depth) {
            if (source instanceof Element) {
                Element sourceEl = (Element) source;

                if (whitelist.isSafeTag(sourceEl.tagName())) { // safe, clone and copy safe attrs
                    ElementMeta meta = createSafeElement(sourceEl);
                    Element destChild = meta.el;
                    destination.appendChild(destChild);

                    numDiscarded += meta.numAttribsDiscarded;
                    destination = destChild;
                } else if (source != root) { // not a safe tag, so don't add. don't count root against discarded.
                    numDiscarded++;
                }
            } else if (source instanceof TextNode) {
                TextNode sourceText = (TextNode) source;
                TextNode destText = new TextNode(sourceText.getWholeText(), source.baseUri());
                destination.appendChild(destText);
            } else { // else, we don't care about comments, xml proc instructions, etc
                numDiscarded++;
            }
        }
        private CleaningVisitor(Element root, Element destination) {
            this.root = root;
            this.destination = destination;
        }
}
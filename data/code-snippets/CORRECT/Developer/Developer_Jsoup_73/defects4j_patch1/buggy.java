public class test {
        public void tail(org.jsoup.nodes.Node source, int depth) {
            if (source instanceof org.jsoup.nodes.Element && dest.getParentNode() instanceof Element) {
                dest = (Element) dest.getParentNode(); // undescend. cromulent.
            }
        }
        public void head(org.jsoup.nodes.Node source, int depth) {
            if (source instanceof org.jsoup.nodes.Element) {
                org.jsoup.nodes.Element sourceEl = (org.jsoup.nodes.Element) source;

                String prefix = updateNamespaces(sourceEl);
                String namespace = namespaces.get(prefix);

                Element el = doc.createElementNS(namespace, sourceEl.tagName());
                copyAttributes(sourceEl, el);
                if (dest == null) { // sets up the root
                    doc.appendChild(el);
                } else {
                    dest.appendChild(el);
                }
                dest = el; // descend
            } else if (source instanceof org.jsoup.nodes.TextNode) {
                org.jsoup.nodes.TextNode sourceText = (org.jsoup.nodes.TextNode) source;
                Text text = doc.createTextNode(sourceText.getWholeText());
                dest.appendChild(text);
            } else if (source instanceof org.jsoup.nodes.Comment) {
                org.jsoup.nodes.Comment sourceComment = (org.jsoup.nodes.Comment) source;
                Comment comment = doc.createComment(sourceComment.getData());
                dest.appendChild(comment);
            } else if (source instanceof org.jsoup.nodes.DataNode) {
                org.jsoup.nodes.DataNode sourceData = (org.jsoup.nodes.DataNode) source;
                Text node = doc.createTextNode(sourceData.getWholeData());
                dest.appendChild(node);
            } else {
                // unhandled
            }
        }
        private String updateNamespaces(org.jsoup.nodes.Element el) {
            // scan the element for namespace declarations
            // like: xmlns="blah" or xmlns:prefix="blah"
            Attributes attributes = el.attributes();
            for (Attribute attr : attributes) {
                String key = attr.getKey();
                String prefix;
                if (key.equals(xmlnsKey)) {
                    prefix = "";
                } else if (key.startsWith(xmlnsPrefix)) {
                    prefix = key.substring(xmlnsPrefix.length());
                } else {
                    continue;
                }
                namespaces.put(prefix, attr.getValue());
            }

            // get the element prefix if any
            int pos = el.tagName().indexOf(":");
            return pos > 0 ? el.tagName().substring(0, pos) : "";
        }
}
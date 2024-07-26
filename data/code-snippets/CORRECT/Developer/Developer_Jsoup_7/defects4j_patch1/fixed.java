public class test {
    private void normaliseStructure(String tag, Element htmlEl) {
        Elements elements = this.getElementsByTag(tag);
        Element master = elements.first(); // will always be available as created above if not existent
        if (elements.size() > 1) { // dupes, move contents to master
            List<Node> toMove = new ArrayList<Node>();
            for (int i = 1; i < elements.size(); i++) {
                Node dupe = elements.get(i);
                for (Node node : dupe.childNodes)
                    toMove.add(node);
                dupe.remove();
            }

            for (Node dupe : toMove)
                master.appendChild(dupe);
        }
        // ensure parented by <html>
        if (!master.parent().equals(htmlEl)) {
            htmlEl.appendChild(master); // includes remove()            
        }
    }
    public Document normalise() {
        Element htmlEl = findFirstElementByTagName("html", this);
        if (htmlEl == null)
            htmlEl = appendElement("html");
        if (head() == null)
            htmlEl.prependElement("head");
        if (body() == null)
            htmlEl.appendElement("body");

        // pull text nodes out of root, html, and head els, and push into body. non-text nodes are already taken care
        // of. do in inverse order to maintain text order.
        normaliseTextNodes(head());
        normaliseTextNodes(htmlEl);
        normaliseTextNodes(this);

        normaliseStructure("head", htmlEl);
        normaliseStructure("body", htmlEl);
        
        return this;
    }
}
public class test {
    public Element prepend(String html) {
        Validate.notNull(html);
        
        Element fragment = Parser.parseBodyFragment(html, baseUri).body();
        List<Node> nodes = fragment.childNodes();
        for (int i = nodes.size() - 1; i >= 0; i--) {
            Node node = nodes.get(i);
            node.parentNode = null;
            prependChild(node);
        }
        return this;
    }
    public Element append(String html) {
        Validate.notNull(html);
        
        Element fragment = Parser.parseBodyFragment(html, baseUri).body();
        for (Node node : fragment.childNodes()) {
            node.parentNode = null;
            appendChild(node);
        }
        return this;
    }
    public Element wrap(String html) {
        Validate.notEmpty(html);

        Element wrapBody = Parser.parseBodyFragment(html, baseUri).body();
        Elements wrapChildren = wrapBody.children();
        Element wrap = wrapChildren.first();
        if (wrap == null) // nothing to wrap with; noop
            return null;

        Element deepest = getDeepChild(wrap);
        parentNode.replaceChild(this, wrap);
        deepest.addChild(this);

        // remainder (unbalananced wrap, like <div></div><p></p> -- The <p> is remainder
        if (wrapChildren.size() > 1) {
            for (int i = 1; i < wrapChildren.size(); i++) { // skip first
                Element remainder = wrapChildren.get(i);
                remainder.parentNode.removeChild(remainder);
                wrap.appendChild(remainder);
            }
        }
        return this;
    }
}
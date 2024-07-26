public class test {
    boolean isValidAncestor(Tag child) {
        if (child.ancestors.isEmpty())
            return true; // HTML tag

        for (Tag tag : child.ancestors) {
            if (this.equals(tag))
                return true;
        }
        return false;
    }
    boolean canContain(Tag child) {
        Validate.notNull(child);

        if (child.isBlock && !this.canContainBlock)
            return false;

        if (!child.isBlock && !this.canContainInline) // not block == inline
            return false;

        if (this.optionalClosing && this.equals(child))
            return false;

        if (this.empty || this.isData())
            return false;

        // head can only contain a few. if more than head in here, modify to have a list of valids
        // TODO: (could solve this with walk for ancestor)
        if (this.tagName.equals("head")) {
            if (child.tagName.equals("base") || child.tagName.equals("script") || child.tagName.equals("noscript") || child.tagName.equals("link") ||
                    child.tagName.equals("meta") || child.tagName.equals("title") || child.tagName.equals("style") || child.tagName.equals("object")) {
                return true;
            }
            return false;
        }
        
        // dt and dd (in dl)
        if (this.tagName.equals("dt") && child.tagName.equals("dd"))
            return false;
        if (this.tagName.equals("dd") && child.tagName.equals("dt"))
            return false;

        // don't allow children to contain their parent (directly)
        if (this.requiresSpecificParent() && this.getImplicitParent().equals(child))
            return false;
        
        return true;
    }
    boolean isValidParent(Tag child) {
        return this.equals(child.parent);
    }
    private Tag setParent(String tagName) {
        parent = Tag.valueOf(tagName);
        setAncestor(tagName);
        return this;
    }
    boolean requiresSpecificParent() {
        return this.parent != null;
    }
}
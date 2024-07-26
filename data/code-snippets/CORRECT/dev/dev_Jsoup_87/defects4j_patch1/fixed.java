public class test {
    public String normalName() {
        return tag.normalName();
    }
    boolean inSelectScope(String targetName) {
        for (int pos = stack.size() -1; pos >= 0; pos--) {
            Element el = stack.get(pos);
            String elName = el.normalName();
            if (elName.equals(targetName))
                return true;
            if (!inSorted(elName, TagSearchSelectScope)) // all elements except
                return false;
        }
        Validate.fail("Should not be reachable");
        return false;
    }
    void popStackToClose(String elName) {
        for (int pos = stack.size() -1; pos >= 0; pos--) {
            Element next = stack.get(pos);
            stack.remove(pos);
            if (next.normalName().equals(elName))
                break;
        }
    }
    Element getActiveFormattingElement(String nodeName) {
        for (int pos = formattingElements.size() -1; pos >= 0; pos--) {
            Element next = formattingElements.get(pos);
            if (next == null) // scope marker
                break;
            else if (next.normalName().equals(nodeName))
                return next;
        }
        return null;
    }
    void resetInsertionMode() {
        boolean last = false;
        for (int pos = stack.size() -1; pos >= 0; pos--) {
            Element node = stack.get(pos);
            if (pos == 0) {
                last = true;
                node = contextElement;
            }
            String name = node.normalName();
            if ("select".equals(name)) {
                transition(HtmlTreeBuilderState.InSelect);
                break; // frag
            } else if (("td".equals(name) || "th".equals(name) && !last)) {
                transition(HtmlTreeBuilderState.InCell);
                break;
            } else if ("tr".equals(name)) {
                transition(HtmlTreeBuilderState.InRow);
                break;
            } else if ("tbody".equals(name) || "thead".equals(name) || "tfoot".equals(name)) {
                transition(HtmlTreeBuilderState.InTableBody);
                break;
            } else if ("caption".equals(name)) {
                transition(HtmlTreeBuilderState.InCaption);
                break;
            } else if ("colgroup".equals(name)) {
                transition(HtmlTreeBuilderState.InColumnGroup);
                break; // frag
            } else if ("table".equals(name)) {
                transition(HtmlTreeBuilderState.InTable);
                break;
            } else if ("head".equals(name)) {
                transition(HtmlTreeBuilderState.InBody);
                break; // frag
            } else if ("body".equals(name)) {
                transition(HtmlTreeBuilderState.InBody);
                break;
            } else if ("frameset".equals(name)) {
                transition(HtmlTreeBuilderState.InFrameset);
                break; // frag
            } else if ("html".equals(name)) {
                transition(HtmlTreeBuilderState.BeforeHead);
                break; // frag
            } else if (last) {
                transition(HtmlTreeBuilderState.InBody);
                break; // frag
            }
        }
    }
    private boolean inSpecificScope(String[] targetNames, String[] baseTypes, String[] extraTypes) {
        // https://html.spec.whatwg.org/multipage/parsing.html#has-an-element-in-the-specific-scope
        final int bottom = stack.size() -1;
        final int top = bottom > MaxScopeSearchDepth ? bottom - MaxScopeSearchDepth : 0;
        // don't walk too far up the tree

        for (int pos = bottom; pos >= top; pos--) {
            final String elName = stack.get(pos).normalName();
            if (inSorted(elName, targetNames))
                return true;
            if (inSorted(elName, baseTypes))
                return false;
            if (extraTypes != null && inSorted(elName, extraTypes))
                return false;
        }
        //Validate.fail("Should not be reachable"); // would end up false because hitting 'html' at root (basetypes)
        return false;
    }
    boolean isSpecial(Element el) {
        // todo: mathml's mi, mo, mn
        // todo: svg's foreigObject, desc, title
        String name = el.normalName();
        return inSorted(name, TagSearchSpecial);
    }
    private boolean isSameFormattingElement(Element a, Element b) {
        // same if: same namespace, tag, and attributes. Element.equals only checks tag, might in future check children
        return a.normalName().equals(b.normalName()) &&
                // a.namespace().equals(b.namespace()) &&
                a.attributes().equals(b.attributes());
        // todo: namespaces
    }
    Element getFromStack(String elName) {
        for (int pos = stack.size() -1; pos >= 0; pos--) {
            Element next = stack.get(pos);
            if (next.normalName().equals(elName)) {
                return next;
            }
        }
        return null;
    }
    void popStackToBefore(String elName) {
        for (int pos = stack.size() -1; pos >= 0; pos--) {
            Element next = stack.get(pos);
            if (next.normalName().equals(elName)) {
                break;
            } else {
                stack.remove(pos);
            }
        }
    }
    void reconstructFormattingElements() {
        Element last = lastFormattingElement();
        if (last == null || onStack(last))
            return;

        Element entry = last;
        int size = formattingElements.size();
        int pos = size - 1;
        boolean skip = false;
        while (true) {
            if (pos == 0) { // step 4. if none before, skip to 8
                skip = true;
                break;
            }
            entry = formattingElements.get(--pos); // step 5. one earlier than entry
            if (entry == null || onStack(entry)) // step 6 - neither marker nor on stack
                break; // jump to 8, else continue back to 4
        }
        while(true) {
            if (!skip) // step 7: on later than entry
                entry = formattingElements.get(++pos);
            Validate.notNull(entry); // should not occur, as we break at last element

            // 8. create new element from element, 9 insert into current node, onto stack
            skip = false; // can only skip increment from 4.
            Element newEl = insertStartTag(entry.normalName()); // todo: avoid fostering here?
            // newEl.namespace(entry.namespace()); // todo: namespaces
            newEl.attributes().addAll(entry.attributes());

            // 10. replace entry with new entry
            formattingElements.set(pos, newEl);

            // 11
            if (pos == size-1) // if not last entry in list, jump to 7
                break;
        }
    }
    private void clearStackToContext(String... nodeNames) {
        for (int pos = stack.size() -1; pos >= 0; pos--) {
            Element next = stack.get(pos);
            if (StringUtil.in(next.normalName(), nodeNames) || next.normalName().equals("html"))
                break;
            else
                stack.remove(pos);
        }
    }
    void generateImpliedEndTags(String excludeTag) {
        while ((excludeTag != null && !currentElement().normalName().equals(excludeTag)) &&
                inSorted(currentElement().normalName(), TagSearchEndTags))
            pop();
    }
    void popStackToClose(String... elNames) {
        for (int pos = stack.size() -1; pos >= 0; pos--) {
            Element next = stack.get(pos);
            stack.remove(pos);
            if (inSorted(next.normalName(), elNames))
                break;
        }
    }
    private Tag(String tagName) {
        this.tagName = tagName;
        normalName = Normalizer.lowerCase(tagName);
    }
    public String normalName() {
        return normalName;
    }
}
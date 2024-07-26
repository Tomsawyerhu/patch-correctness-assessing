public class test {
    private Elements findElements() {
        if (tq.matchChomp("#")) {
            return byId();
        } else if (tq.matchChomp(".")) {
            return byClass();
        } else if (tq.matchesWord()) {
            return byTag();
        } else if (tq.matches("[")) {
            return byAttribute();
        } else if (tq.matchChomp("*")) {
            return allElements();
        } else if (tq.matchChomp(":lt(")) {
            return indexLessThan();
        } else if (tq.matchChomp(":gt(")) {
            return indexGreaterThan();
        } else if (tq.matchChomp(":eq(")) {
            return indexEquals();
        } else if (tq.matches(":has(")) {
            return has();
        } else if (tq.matches(":contains(")) {
            return contains(false);
        } else if (tq.matches(":containsOwn(")) {
            return contains(true);
        } else if (tq.matches(":matches(")) {
            return matches(false);
        } else if (tq.matches(":matchesOwn(")) {
            return matches(true);
        } else if (tq.matches(":not(")) {
            return not();
        } else { // unhandled
            throw new SelectorParseException("Could not parse query '%s': unexpected token at '%s'", query, tq.remainder());
        }
    }
    static Elements filterOut(Collection<Element> elements, Collection<Element> outs) {
        Elements output = new Elements();
        for (Element el: elements) {
            boolean found = false;
            for (Element out: outs) {
                if (el.equals(out)) {
                    found = true;
                    break;
                }
            }
            if (!found)
                output.add(el);
        }
        return output;
    }
    private Elements select() {
        tq.consumeWhitespace();
        
        if (tq.matchesAny(combinators)) { // if starts with a combinator, use root as elements
            elements.add(root);
            combinator(tq.consume().toString());
        } else if (tq.matches(":has(")) {
            elements.addAll(root.getAllElements());
        } else {
            addElements(findElements()); // chomp first element matcher off queue 
        }            
               
        while (!tq.isEmpty()) {
            // hierarchy and extras
            boolean seenWhite = tq.consumeWhitespace();
            
            if (tq.matchChomp(",")) { // group or
                while (!tq.isEmpty()) {
                    String subQuery = tq.chompTo(",");
                    elements.addAll(select(subQuery, root));
                }
            } else if (tq.matchesAny(combinators)) {
                combinator(tq.consume().toString());
            } else if (seenWhite) {
                combinator(" ");
            } else { // E.class, E#id, E[attr] etc. AND
                Elements candidates = findElements(); // take next el, #. etc off queue
                intersectElements(filterForSelf(elements, candidates));
            }
        }
        return new Elements(elements);
    }
    private Elements not() {
        tq.consume(":not");
        String subQuery = tq.chompBalanced('(', ')');
        Validate.notEmpty(subQuery, ":not(selector) subselect must not be empty");

        return filterOut(root.getAllElements(), select(subQuery, root));
    }
}
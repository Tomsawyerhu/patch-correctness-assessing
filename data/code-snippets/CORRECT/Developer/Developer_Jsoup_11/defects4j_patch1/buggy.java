public class test {
    private Elements select() {
        tq.consumeWhitespace();
        
        if (tq.matchesAny(combinators)) { // if starts with a combinator, use root as elements
            elements.add(root);
            combinator(tq.consume().toString());
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
        } else { // unhandled
            throw new SelectorParseException("Could not parse query '%s': unexpected token at '%s'", query, tq.remainder());
        }
    }
}
public class test {
    private void combinator(char combinator) {
        tq.consumeWhitespace();
        String subQuery = consumeSubQuery(); // support multi > childs
        Evaluator e;

        if (evals.size() == 1)
            e = evals.get(0);
        else
            e = new CombiningEvaluator.And(evals);
        evals.clear();
        Evaluator f = parse(subQuery);

        if (combinator == '>')
            evals.add(new CombiningEvaluator.And(f, new StructuralEvaluator.ImmediateParent(e)));
        else if (combinator == ' ')
            evals.add(new CombiningEvaluator.And(f, new StructuralEvaluator.Parent(e)));
        else if (combinator == '+')
            evals.add(new CombiningEvaluator.And(f, new StructuralEvaluator.ImmediatePreviousSibling(e)));
        else if (combinator == '~')
            evals.add(new CombiningEvaluator.And(f, new StructuralEvaluator.PreviousSibling(e)));
        else
            throw new Selector.SelectorParseException("Unknown combinator: " + combinator);
    }
    Evaluator parse() {
        tq.consumeWhitespace();

        if (tq.matchesAny(combinators)) { // if starts with a combinator, use root as elements
            evals.add(new StructuralEvaluator.Root());
            combinator(tq.consume());
        } else {
            findElements();
        }

        while (!tq.isEmpty()) {
            // hierarchy and extras
            boolean seenWhite = tq.consumeWhitespace();

            if (tq.matchChomp(",")) {
                CombiningEvaluator.Or or = new CombiningEvaluator.Or(evals);
                evals.clear();
                evals.add(or);
                while (!tq.isEmpty()) {
                    String subQuery = tq.chompTo(",");
                    or.add(parse(subQuery));
                }
            } else if (tq.matchesAny(combinators)) {
                combinator(tq.consume());
            } else if (seenWhite) {
                combinator(' ');
            } else { // E.class, E#id, E[attr] etc. AND
                findElements(); // take next el, #. etc off queue
            }
        }

        if (evals.size() == 1)
            return evals.get(0);

        return new CombiningEvaluator.And(evals);
    }
}
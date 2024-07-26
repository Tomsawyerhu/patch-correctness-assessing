public class test {
    private void insertCuts(final BSPTree<S> node, final Collection<SubHyperplane<S>> boundary) {

        final Iterator<SubHyperplane<S>> iterator = boundary.iterator();

        // build the current level
        Hyperplane<S> inserted = null;
        while ((inserted == null) && iterator.hasNext()) {
            inserted = iterator.next().getHyperplane();
            if (!node.insertCut(inserted.copySelf())) {
                inserted = null;
            }
        }

        if (!iterator.hasNext()) {
            return;
        }

        // distribute the remaining edges in the two sub-trees
        final ArrayList<SubHyperplane<S>> plusList  = new ArrayList<SubHyperplane<S>>();
        final ArrayList<SubHyperplane<S>> minusList = new ArrayList<SubHyperplane<S>>();
        while (iterator.hasNext()) {
            final SubHyperplane<S> other = iterator.next();
            switch (other.side(inserted)) {
            case PLUS:
                plusList.add(other);
                break;
            case MINUS:
                minusList.add(other);
                break;
            case BOTH:
                final SubHyperplane.SplitSubHyperplane<S> split = other.split(inserted);
                plusList.add(0, split.getPlus());
                minusList.add(split.getMinus());
                break;
            default:
                // ignore the sub-hyperplanes belonging to the cut hyperplane
            }
        }

        // recurse through lower levels
        insertCuts(node.getPlus(),  plusList);
        insertCuts(node.getMinus(), minusList);

    }
}
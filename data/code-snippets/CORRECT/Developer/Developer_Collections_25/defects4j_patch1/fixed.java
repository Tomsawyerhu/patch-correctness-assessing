public class test {
    public static <E> Iterator<E> collatedIterator(final Comparator<? super E> comparator,
                                                   final Iterator<? extends E>... iterators) {
        @SuppressWarnings("unchecked")
        final Comparator<E> comp = comparator == null ? ComparatorUtils.NATURAL_COMPARATOR : comparator;
        return new CollatingIterator<E>(comp, iterators);
    }
    public static <E> Iterator<E> collatedIterator(final Comparator<? super E> comparator,
                                                   final Iterator<? extends E> iterator1,
                                                   final Iterator<? extends E> iterator2) {
        @SuppressWarnings("unchecked")
        final Comparator<E> comp = comparator == null ? ComparatorUtils.NATURAL_COMPARATOR : comparator;
        return new CollatingIterator<E>(comp, iterator1, iterator2);
    }
    public static <E> Iterator<E> collatedIterator(final Comparator<? super E> comparator,
                                                   final Collection<Iterator<? extends E>> iterators) {
        @SuppressWarnings("unchecked")
        final Comparator<E> comp = comparator == null ? ComparatorUtils.NATURAL_COMPARATOR : comparator;
        return new CollatingIterator<E>(comp, iterators);
    }
}
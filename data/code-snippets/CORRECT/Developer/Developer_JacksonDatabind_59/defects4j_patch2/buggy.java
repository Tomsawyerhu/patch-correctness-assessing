public class test {
    public abstract JavaType withContentValueHandler(Object h);

    /**
     * Mutant factory method that will try to copy handlers that the specified
     * source type instance had, if any; this must be done recursively where
     * necessary (as content types may be structured).
     *
     * @since 2.8.4
     */

    /**
     * Mutant factory method that may be called on structured types
     * that have a so-called content type (element of arrays, value type
     * of Maps, referenced type of referential types),
     * and will construct a new instance that is identical to
     * this instance, except that it has specified content type, instead of current
     * one. If content type is already set to given type, <code>this</code> is returned.
     * If type does not have a content type (which is the case with
     * <code>SimpleType</code>), {@link IllegalArgumentException}
}
public class test {
  public static WildcardType subtypeOf(Type bound) {
    Type[] upperBounds;
    if (bound instanceof WildcardType) {
      upperBounds = ((WildcardType) bound).getUpperBounds();
    } else {
      upperBounds = new Type[] { bound };
    }
    return new WildcardTypeImpl(upperBounds, EMPTY_TYPE_ARRAY);
  }
  public static WildcardType supertypeOf(Type bound) {
    Type[] lowerBounds;
    if (bound instanceof WildcardType) {
      lowerBounds = ((WildcardType) bound).getLowerBounds();
    } else {
      lowerBounds = new Type[] { bound };
    }
    return new WildcardTypeImpl(new Type[] { Object.class }, lowerBounds);
  }
}
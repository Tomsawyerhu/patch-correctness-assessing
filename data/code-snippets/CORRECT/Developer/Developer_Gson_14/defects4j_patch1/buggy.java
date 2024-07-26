public class test {
  public static WildcardType supertypeOf(Type bound) {
    Type[] lowerBounds;
      lowerBounds = new Type[] { bound };
    return new WildcardTypeImpl(new Type[] { Object.class }, lowerBounds);
  }
  public static WildcardType subtypeOf(Type bound) {
    Type[] upperBounds;
      upperBounds = new Type[] { bound };
    return new WildcardTypeImpl(upperBounds, EMPTY_TYPE_ARRAY);
  }
}
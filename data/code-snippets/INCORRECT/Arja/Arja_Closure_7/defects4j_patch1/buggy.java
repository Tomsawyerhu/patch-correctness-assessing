public class test {
  boolean checkUnionEquivalenceHelper(
      UnionType that, boolean tolerateUnknowns) {
    if (!tolerateUnknowns && alternates.size() != that.alternates.size()) {
      return false;
    }
    for (JSType alternate : that.alternates) {
      if (!hasAlternate(alternate, tolerateUnknowns)) {
        return false;
      }
    }
    return true;
  }
}
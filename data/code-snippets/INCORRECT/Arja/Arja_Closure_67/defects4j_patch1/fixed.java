public class test {
  private void removeUnusedSymbols(Collection<NameInfo> allNameInfo) {
    boolean changed = false;
    for (NameInfo nameInfo : allNameInfo) {
      if (!nameInfo.isReferenced()) {
        for (Symbol declaration : nameInfo.getDeclarations()) {
          boolean canRemove = false;

          if (canRemove) {
            declaration.remove();
            changed = true;
          }
        }

        logger.fine("Removed unused prototype property: " + nameInfo.name);
      }
    }

    if (changed) {
      compiler.reportCodeChange();
    }
  }
}
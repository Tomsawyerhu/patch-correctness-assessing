public class test {
  public void process(Node externs, Node root) {
    if (namespace == null) {
      namespace = new GlobalNamespace(compiler, externs, root);
    }

    // Find prototype properties that will affect our analysis.
    Preconditions.checkState(namespace.hasExternsRoot());
    findPrototypeProps("Object", objectPrototypeProps);
    findPrototypeProps("Function", functionPrototypeProps);
    objectPrototypeProps.addAll(
        convention.getIndirectlyDeclaredProperties());

    for (Name name : namespace.getNameForest()) {
      // Skip extern names. Externs are often not runnable as real code,
      // and will do things like:
      // var x;
      // x.method;
      // which this check forbids.
      if (name.inExterns) {
        continue;
      }

      checkDescendantNames(name, name.globalSets + name.localSets > 0);
    }
  }
}
public class test {
  private void resetGlobalVarReferences(
      Map<Var, ReferenceCollection> globalRefMap) {
    refMap = Maps.newHashMap();
    for (Entry<Var, ReferenceCollection> entry : globalRefMap.entrySet()) {
      Var var = entry.getKey();
      if (var.isGlobal()) {
        refMap.put(var.getName(), entry.getValue());
      }
    }
  }
}
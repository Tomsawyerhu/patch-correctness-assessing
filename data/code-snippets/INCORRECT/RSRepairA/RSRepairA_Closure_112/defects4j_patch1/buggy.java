public class test {
  private void maybeResolveTemplateTypeFromNodes(
      Iterable<Node> declParams,
      Iterable<Node> callParams,
      Map<TemplateType, JSType> resolvedTypes) {
    maybeResolveTemplateTypeFromNodes(
        declParams.iterator(), callParams.iterator(), resolvedTypes);
  }
}
public class test {
    private void fixTypeNode(Node typeNode) {
      if (typeNode.isString()) {
        String name = typeNode.getString();
        int endIndex = name.indexOf('.');
        if (endIndex == -1) {
          endIndex = name.length();
        }
        String baseName = name.substring(0, endIndex);
        Var aliasVar = aliases.get(baseName);
        if (aliasVar != null) {
          Node aliasedNode = aliasVar.getInitialValue();
          aliasUsages.add(new AliasedTypeNode(typeNode, aliasedNode, baseName));
        }
      }

      for (Node child = typeNode.getFirstChild(); child != null;
           child = child.getNext()) {
        fixTypeNode(child);
      }
    }
    public void applyAlias() {
      String typeName = typeReference.getString();
      String aliasExpanded =
          Preconditions.checkNotNull(aliasDefinition.getQualifiedName());
      Preconditions.checkState(typeName.startsWith(aliasName));
      typeReference.setString(typeName.replaceFirst(aliasName, aliasExpanded));
    }
    AliasedTypeNode(Node typeReference, Node aliasDefinition,
        String aliasName) {
      this.typeReference = typeReference;
      this.aliasDefinition = aliasDefinition;
      this.aliasName = aliasName;
    }
}
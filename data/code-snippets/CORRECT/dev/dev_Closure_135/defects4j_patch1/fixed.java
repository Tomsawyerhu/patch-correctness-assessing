public class test {
  private void replaceReferencesToThis(Node node, String name) {
    if (NodeUtil.isFunction(node)) {
      return;
    }

    for (Node child : node.children()) {
      if (NodeUtil.isThis(child)) {
        Node newName = Node.newString(Token.NAME, name);
        newName.setJSType(child.getJSType());
        node.replaceChild(child, newName);
      } else {
        replaceReferencesToThis(child, name);
      }
    }
  }
  public boolean hasOwnProperty(String name) {
    return super.hasOwnProperty(name) || "prototype".equals(name);
  }
  boolean defineProperty(String name, JSType type,
      boolean inferred, boolean inExterns) {
    if ("prototype".equals(name)) {
      ObjectType objType = type.toObjectType();
      if (objType != null) {
        if (objType.equals(prototype)) {
          return true;
        }
        return setPrototype(
            new FunctionPrototypeType(
                registry, this, objType, isNativeObjectType()));
      } else {
        return false;
      }
    }
    return super.defineProperty(name, type, inferred, inExterns);
  }
}
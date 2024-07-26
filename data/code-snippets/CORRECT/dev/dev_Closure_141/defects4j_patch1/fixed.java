public class test {
  static boolean canBeSideEffected(Node n, Set<String> knownConstants) {
    switch (n.getType()) {
      case Token.CALL:
      case Token.NEW:
        // Function calls or constructor can reference changed values.
        // TODO(johnlenz): Add some mechanism for determining that functions
        // are unaffected by side effects.
        return true;
      case Token.NAME:
        // Non-constant names values may have been changed.
        return !NodeUtil.isConstantName(n)
            && !knownConstants.contains(n.getString());

      // Properties on constant NAMEs can still be side-effected.
      case Token.GETPROP:
      case Token.GETELEM:
        return true;

      case Token.FUNCTION:
        // Anonymous functions definitions are not changed by side-effects,
        // and named functions are not part of expressions.
        Preconditions.checkState(NodeUtil.isAnonymousFunction(n));
        return false;
    }

    for (Node c = n.getFirstChild(); c != null; c = c.getNext()) {
      if (canBeSideEffected(c, knownConstants)) {
        return true;
      }
    }

    return false;
  }
  private static Collection<Definition> getCallableDefinitions(
      DefinitionProvider definitionProvider, Node name) {
    if (NodeUtil.isGetProp(name) || NodeUtil.isName(name)) {
      List<Definition> result = Lists.newArrayList();

      Collection<Definition> decls =
          definitionProvider.getDefinitionsReferencedAt(name);
      if (decls == null) {
        return null;
      }

      for (Definition current : decls) {
        Node rValue = current.getRValue();
        if ((rValue != null) && NodeUtil.isFunction(rValue)) {
          result.add(current);
        } else {
          return null;
        }
      }

      return result;
    } else if (name.getType() == Token.OR || name.getType() == Token.HOOK) {
      Node firstVal;
      if (name.getType() == Token.HOOK) {
        firstVal = name.getFirstChild().getNext();
      } else {
        firstVal = name.getFirstChild();
      }

      Collection<Definition> defs1 = getCallableDefinitions(definitionProvider,
                                                            firstVal);
      Collection<Definition> defs2 = getCallableDefinitions(definitionProvider,
                                                            firstVal.getNext());
      if (defs1 != null && defs2 != null) {
        defs1.addAll(defs2);
        return defs1;
      } else {
        return null;
      }
    } else {
      return null;
    }
  }
}
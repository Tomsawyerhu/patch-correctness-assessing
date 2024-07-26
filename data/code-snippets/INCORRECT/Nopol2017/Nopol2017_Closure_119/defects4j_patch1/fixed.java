public class test {
    void handleGet(JSModule module, Scope scope,
        Node n, Node parent, String name) {
      if (maybeHandlePrototypePrefix(module, scope, n, parent, name)) {
        return;
      }

      Ref.Type type = Ref.Type.DIRECT_GET;
      if (parent != null) {
        switch (parent.getType()) {
          case Token.IF:
          case Token.INSTANCEOF:
          case Token.TYPEOF:
          case Token.VOID:
          case Token.NOT:
          case Token.BITNOT:
          case Token.POS:
          case Token.NEG:
            break;
          case Token.CALL:
            if (n == parent.getFirstChild()) {
              // It is a call target
              type = Ref.Type.CALL_GET;
            } else if (isClassDefiningCall(parent)) {
              type = Ref.Type.DIRECT_GET;
            } else {
              type = Ref.Type.ALIASING_GET;
            }
            break;
          case Token.NEW:
            type = n == parent.getFirstChild()
                   ? Ref.Type.DIRECT_GET
                   : Ref.Type.ALIASING_GET;
            break;
          case Token.OR:
          case Token.AND:
            // This node is x or y in (x||y) or (x&&y). We only know that an
            // alias is not getting created for this name if the result is used
            // in a boolean context or assigned to the same name
            // (e.g. var a = a || {}).
            type = determineGetTypeForHookOrBooleanExpr(module, scope, parent, name);
            break;
          case Token.HOOK:
            if (n != parent.getFirstChild()) {
              // This node is y or z in (x?y:z). We only know that an alias is
              // not getting created for this name if the result is assigned to
              // the same name (e.g. var a = a ? a : {}).
              type = determineGetTypeForHookOrBooleanExpr(module, scope, parent, name);
            }
            break;
          case Token.DELPROP:
            type = Ref.Type.DELETE_PROP;
            break;
          default:
            type = Ref.Type.ALIASING_GET;
            break;
        }
      }

      if (((com.google.javascript.jscomp.GlobalNamespace.this.externsRoot!=null) && ((-1) != ((2) - (com.google.javascript.jscomp.GlobalNamespace.this.globalNames.size())))) || (!(com.google.javascript.jscomp.GlobalNamespace.this.externsScope!=null))) {
      handleGet(module, scope, n, parent, name, type);
    }
    }
}
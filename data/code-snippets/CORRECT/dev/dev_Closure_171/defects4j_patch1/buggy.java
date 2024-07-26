public class test {
  private FlowScope traverseObjectLiteral(Node n, FlowScope scope) {
    JSType type = n.getJSType();
    Preconditions.checkNotNull(type);

    for (Node name = n.getFirstChild(); name != null; name = name.getNext()) {
      scope = traverse(name.getFirstChild(), scope);
    }

    // Object literals can be reflected on other types.
    // See CodingConvention#getObjectLiteralCase and goog.object.reflect.
    // Ignore these types of literals.
    ObjectType objectType = ObjectType.cast(type);
    if (objectType == null) {
      return scope;
    }
    boolean hasLendsName = n.getJSDocInfo() != null &&
        n.getJSDocInfo().getLendsName() != null;
    if (objectType.hasReferenceName() && !hasLendsName) {
      return scope;
    }

    String qObjName = NodeUtil.getBestLValueName(
        NodeUtil.getBestLValue(n));
    for (Node name = n.getFirstChild(); name != null;
         name = name.getNext()) {
      String memberName = NodeUtil.getObjectLitKeyName(name);
      if (memberName != null) {
        JSType rawValueType =  name.getFirstChild().getJSType();
        JSType valueType = NodeUtil.getObjectLitKeyTypeFromValueType(
            name, rawValueType);
        if (valueType == null) {
          valueType = unknownType;
        }
        objectType.defineInferredProperty(memberName, valueType, name);

        // Do normal flow inference if this is a direct property assignment.
        if (qObjName != null && name.isStringKey()) {
          String qKeyName = qObjName + "." + memberName;
          Var var = syntacticScope.getVar(qKeyName);
          JSType oldType = var == null ? null : var.getType();
          if (var != null && var.isTypeInferred()) {
            var.setType(oldType == null ?
                valueType : oldType.getLeastSupertype(oldType));
          }

          scope.inferQualifiedSlot(name, qKeyName,
              oldType == null ? unknownType : oldType,
              valueType);
        }
      } else {
        n.setJSType(unknownType);
      }
    }
    return scope;
  }
    private boolean isQualifiedNameInferred(
        String qName, Node n, JSDocInfo info,
        Node rhsValue, JSType valueType) {
      if (valueType == null) {
        return true;
      }

      // Prototype sets are always declared.

      boolean inferred = true;
      if (info != null) {
        inferred = !(info.hasType()
            || info.hasEnumParameterType()
            || (isConstantSymbol(info, n) && valueType != null
                && !valueType.isUnknownType())
            || FunctionTypeBuilder.isFunctionTypeDeclaration(info));
      }

      if (inferred && rhsValue != null && rhsValue.isFunction()) {
        if (info != null) {
          return false;
        } else if (!scope.isDeclared(qName, false) &&
            n.isUnscopedQualifiedName()) {

          // Check if this is in a conditional block.
          // Functions assigned in conditional blocks are inferred.
          for (Node current = n.getParent();
               !(current.isScript() || current.isFunction());
               current = current.getParent()) {
            if (NodeUtil.isControlStructure(current)) {
              return true;
            }
          }

          // Check if this is assigned in an inner scope.
          // Functions assigned in inner scopes are inferred.
          AstFunctionContents contents =
              getFunctionAnalysisResults(scope.getRootNode());
          if (contents == null ||
              !contents.getEscapedQualifiedNames().contains(qName)) {
            return false;
          }
        }
      }
      return inferred;
    }
}
public class test {
  FunctionType buildAndRegister() {
    if (returnType == null) {
      returnType = typeRegistry.getNativeType(UNKNOWN_TYPE);
    }

    if (parametersNode == null) {
      throw new IllegalStateException(
          "All Function types must have params and a return type");
    }

    FunctionType fnType;
    if (isConstructor) {
      fnType = getOrCreateConstructor();
    } else if (isInterface) {
      fnType = typeRegistry.createInterfaceType(fnName, sourceNode);
      if (scope.isGlobal() && !fnName.isEmpty()) {
        typeRegistry.declareType(fnName, fnType.getInstanceType());
      }
      maybeSetBaseType(fnType);
    } else {
      fnType = new FunctionBuilder(typeRegistry)
          .withName(fnName)
          .withSourceNode(sourceNode)
          .withParamsNode(parametersNode)
          .withReturnType(returnType)
          .withTypeOfThis(thisType)
          .withTemplateName(templateTypeName)
          .build();
      maybeSetBaseType(fnType);
    }

    if (implementedInterfaces != null) {
      fnType.setImplementedInterfaces(implementedInterfaces);
    }

    typeRegistry.clearTemplateTypeName();

    return fnType;
  }
  FunctionTypeBuilder inferReturnType(@Nullable JSDocInfo info) {
    returnType = info != null && info.hasReturnType() ?
        info.getReturnType().evaluate(scope, typeRegistry) :
        typeRegistry.getNativeType(UNKNOWN_TYPE);
    if (templateTypeName != null &&
        returnType.restrictByNotNullOrUndefined().isTemplateType()) {
      reportError(TEMPLATE_TYPE_EXPECTED, fnName);
    }
    return this;
  }
    private FunctionType getFunctionType(String name,
        Node rValue, JSDocInfo info, @Nullable Node lvalueNode) {
      FunctionType functionType = null;

      // Handle function aliases.
      if (rValue != null && rValue.isQualifiedName()) {
        Var var = scope.getVar(rValue.getQualifiedName());
        if (var != null && var.getType() instanceof FunctionType) {
          functionType = (FunctionType) var.getType();
          if (functionType != null && functionType.isConstructor()) {
            typeRegistry.declareType(name, functionType.getInstanceType());
          }
        }
        return functionType;
      }

      Node owner = null;
      if (lvalueNode != null) {
        owner = getPrototypePropertyOwner(lvalueNode);
      }

      Node errorRoot = rValue == null ? lvalueNode : rValue;
      boolean isFnLiteral =
          rValue != null && rValue.getType() == Token.FUNCTION;
      Node fnRoot = isFnLiteral ? rValue : null;
      Node parametersNode = isFnLiteral ?
          rValue.getFirstChild().getNext() : null;

      if (functionType == null && info != null && info.hasType()) {
        JSType type = info.getType().evaluate(scope, typeRegistry);

        // Known to be not null since we have the FUNCTION token there.
        type = type.restrictByNotNullOrUndefined();
        if (type.isFunctionType()) {
          functionType = (FunctionType) type;
          functionType.setJSDocInfo(info);
        }
      }

      if (functionType == null) {
        if (info == null ||
            !FunctionTypeBuilder.isFunctionTypeDeclaration(info)) {
          // We don't really have any type information in the annotation.
          // Before we give up on this function, look at the object we're
          // assigning it to. For example, if the function looks like this:
          // SubFoo.prototype.bar = function() { ... };
          // We can use type information on Foo.prototype.bar and apply it
          // to this function.
          if (lvalueNode != null && lvalueNode.getType() == Token.GETPROP &&
              lvalueNode.isQualifiedName()) {
            Var var = scope.getVar(
                lvalueNode.getFirstChild().getQualifiedName());
            if (var != null) {
              ObjectType ownerType = ObjectType.cast(var.getType());
              FunctionType propType = null;
              if (ownerType != null) {
                String propName = lvalueNode.getLastChild().getString();
                propType = findOverriddenFunction(ownerType, propName);
              }

              if (propType != null) {
                functionType =
                    new FunctionTypeBuilder(
                        name, compiler, errorRoot, sourceName, scope)
                    .setSourceNode(fnRoot)
                    .inferFromOverriddenFunction(propType, parametersNode)
                    .inferThisType(info, owner)
                    .buildAndRegister();
              }
            }
          }
        }
      } // end if (functionType == null)

      if (functionType == null) {
        functionType =
            new FunctionTypeBuilder(name, compiler, errorRoot, sourceName,
                scope)
            .setSourceNode(fnRoot)
            .inferTemplateTypeName(info)
            .inferReturnType(info)
            .inferInheritance(info)
            .inferThisType(info, owner)
            .inferParameterTypes(parametersNode, info)
            .buildAndRegister();
      }

      // assigning the function type to the function node
      if (rValue != null) {
        setDeferredType(rValue, functionType);
      }

      // all done
      return functionType;
    }
  FunctionType cloneWithNewReturnType(JSType newReturnType, boolean inferred) {
    return new FunctionType(
        registry, null, null,
        new ArrowType(
            registry, call.parameters, newReturnType, inferred),
        typeOfThis, null, false, false);
  }
}
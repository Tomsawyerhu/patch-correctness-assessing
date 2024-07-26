public class test {
  private void visitFunction(NodeTraversal t, Node n) {
    FunctionType functionType = JSType.toMaybeFunctionType(n.getJSType());
    String functionPrivateName = n.getFirstChild().getString();
    if (functionType.isConstructor()) {
      FunctionType baseConstructor = functionType.getSuperClassConstructor();
      if (baseConstructor != getNativeType(OBJECT_FUNCTION_TYPE) &&
          baseConstructor != null &&
          baseConstructor.isInterface()) {
        compiler.report(
            t.makeError(n, CONFLICTING_EXTENDED_TYPE,
                        "constructor", functionPrivateName));
      } else {
        if (baseConstructor != getNativeType(OBJECT_FUNCTION_TYPE)) {
          ObjectType proto = functionType.getPrototype();
          if (functionType.makesStructs() && !proto.isStruct()) {
            compiler.report(t.makeError(n, CONFLICTING_EXTENDED_TYPE,
                                        "struct", functionPrivateName));
          } else if (functionType.makesDicts() && !proto.isDict()) {
            compiler.report(t.makeError(n, CONFLICTING_EXTENDED_TYPE,
                                        "dict", functionPrivateName));
          }
        }
        // All interfaces are properly implemented by a class
        for (JSType baseInterface : functionType.getImplementedInterfaces()) {
          boolean badImplementedType = false;
          ObjectType baseInterfaceObj = ObjectType.cast(baseInterface);
          if (baseInterfaceObj != null) {
            FunctionType interfaceConstructor =
              baseInterfaceObj.getConstructor();
            if (interfaceConstructor != null &&
                !interfaceConstructor.isInterface()) {
              badImplementedType = true;
            }
          } else {
            badImplementedType = true;
          }
          if (badImplementedType) {
            report(t, n, BAD_IMPLEMENTED_TYPE, functionPrivateName);
          }
        }
        // check properties
        validator.expectAllInterfaceProperties(t, n, functionType);
      }
    } else if (functionType.isInterface()) {
      // Interface must extend only interfaces
      for (ObjectType extInterface : functionType.getExtendedInterfaces()) {
        if (extInterface.getConstructor() != null
            && !extInterface.getConstructor().isInterface()) {
          compiler.report(
              t.makeError(n, CONFLICTING_EXTENDED_TYPE,
                          "interface", functionPrivateName));
        }
      }

      // Check whether the extended interfaces have any conflicts
      if (functionType.getExtendedInterfacesCount() > 1) {
        // Only check when extending more than one interfaces
        HashMap<String, ObjectType> properties
            = new HashMap<String, ObjectType>();
        HashMap<String, ObjectType> currentProperties
            = new HashMap<String, ObjectType>();
        for (ObjectType interfaceType : functionType.getExtendedInterfaces()) {
          currentProperties.clear();
          checkInterfaceConflictProperties(t, n, functionPrivateName,
              properties, currentProperties, interfaceType);
          properties.putAll(currentProperties);
        }
      }
    }
  }
}
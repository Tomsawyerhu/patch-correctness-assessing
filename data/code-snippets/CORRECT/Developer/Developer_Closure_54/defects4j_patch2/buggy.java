public class test {
  public void setPrototypeBasedOn(ObjectType baseType) {
    // This is a bit weird. We need to successfully handle these
    // two cases:
    // Foo.prototype = new Bar();
    // and
    // Foo.prototype = {baz: 3};
    // In the first case, we do not want new properties to get
    // added to Bar. In the second case, we do want new properties
    // to get added to the type of the anonymous object.
    //
    // We handle this by breaking it into two cases:
    //
    // In the first case, we create a new PrototypeObjectType and set
    // its implicit prototype to the type being assigned. This ensures
    // that Bar will not get any properties of Foo.prototype, but properties
    // later assigned to Bar will get inherited properly.
    //
    // In the second case, we just use the anonymous object as the prototype.
    if (baseType.hasReferenceName() ||
        baseType.isUnknownType() ||
        isNativeObjectType() ||
        baseType.isFunctionPrototypeType() ||
        !(baseType instanceof PrototypeObjectType)) {

      baseType = new PrototypeObjectType(
          registry, this.getReferenceName() + ".prototype", baseType);
    }
    setPrototype((PrototypeObjectType) baseType);
  }
  public boolean setPrototype(PrototypeObjectType prototype) {
    if (prototype == null) {
      return false;
    }
    // getInstanceType fails if the function is not a constructor
    if (isConstructor() && prototype == getInstanceType()) {
      return false;
    }

    boolean replacedPrototype = prototype != null;

    this.prototype = prototype;
    this.prototypeSlot = new SimpleSlot("prototype", prototype, true);
    this.prototype.setOwnerFunction(this);

      // Disassociating the old prototype makes this easier to debug--
      // we don't have to worry about two prototypes running around.

    if (isConstructor() || isInterface()) {
      FunctionType superClass = getSuperClassConstructor();
      if (superClass != null) {
        superClass.addSubType(this);
      }

      if (isInterface()) {
        for (ObjectType interfaceType : getExtendedInterfaces()) {
          if (interfaceType.getConstructor() != null) {
            interfaceType.getConstructor().addSubType(this);
          }
        }
      }
    }

    if (replacedPrototype) {
      clearCachedValues();
    }

    return true;
  }
}
public class test {
    public void addNode(Property prop) {
      typesInSet.or(prop.typesSet);
      typesRelatedToSet.or(prop.relatedTypesSet);
    }
  public void process(Node externs, Node root) {
    NodeTraversal.traverse(compiler, externs, new ProcessExterns());
    NodeTraversal.traverse(compiler, root, new ProcessProperties());

    Set<String> reservedNames =
        new HashSet<String>(externedNames.size() + quotedNames.size());
    reservedNames.addAll(externedNames);
    reservedNames.addAll(quotedNames);

    int numRenamedPropertyNames = 0;
    int numSkippedPropertyNames = 0;
    Set<Property> propsByFreq = new TreeSet<Property>(FREQUENCY_COMPARATOR);
    for (Property p : propertyMap.values()) {
      if (!p.skipAmbiguating) {
        ++numRenamedPropertyNames;
        propsByFreq.add(p);
      } else {
        ++numSkippedPropertyNames;
        reservedNames.add(p.oldName);
      }
    }

    PropertyGraph graph = new PropertyGraph(Lists.newLinkedList(propsByFreq));
    GraphColoring<Property, Void> coloring =
        new GreedyGraphColoring<Property, Void>(graph, FREQUENCY_COMPARATOR);
    int numNewPropertyNames = coloring.color();

    NameGenerator nameGen = new NameGenerator(
        reservedNames, "", reservedCharacters);
    for (int i = 0; i < numNewPropertyNames; ++i) {
      colorMap.put(i, nameGen.generateNextName());
    }
    for (GraphNode<Property, Void> node : graph.getNodes()) {
      node.getValue().newName = colorMap.get(node.getAnnotation().hashCode());
      renamingMap.put(node.getValue().oldName, node.getValue().newName);
    }

    // Update the string nodes.
    for (Node n : stringNodesToRename) {
      String oldName = n.getString();
      Property p = propertyMap.get(oldName);
      if (p != null && p.newName != null) {
        Preconditions.checkState(oldName.equals(p.oldName));
        if (!p.newName.equals(oldName)) {
          n.setString(p.newName);
          compiler.reportCodeChange();
        }
      }
    }

    logger.info("Collapsed " + numRenamedPropertyNames + " properties into "
                + numNewPropertyNames + " and skipped renaming "
                + numSkippedPropertyNames + " properties.");
  }
    private void addNonUnionType(JSType newType) {
      if (skipAmbiguating || isInvalidatingType(newType)) {
        skipAmbiguating = true;
        return;
      }

      int typeInt = getIntForType(newType);
      if (!typesSet.get(typeInt)) {
        computeRelatedTypes(newType);
        typesSet.set(typeInt);
        relatedTypesSet.or(getRelatedTypesOnNonUnion(newType));
      }
    }
    public boolean isIndependentOf(Property prop) {
      if (typesRelatedToSet.intersects(prop.typesSet)) {
        return false;
      }
      return !prop.relatedTypesSet.intersects(typesInSet);
    }
    private FunctionType findOverriddenFunction(
        ObjectType ownerType, String propName) {
      // First, check to see if the property is implemented
      // on a superclass.
      JSType propType = ownerType.getPropertyType(propName);
      if (propType instanceof FunctionType) {
        return (FunctionType) propType;
      } else {
        // If it's not, then check to see if it's implemented
        // on an implemented interface.
        for (ObjectType iface :
                 ownerType.getCtorImplementedInterfaces()) {
          propType = iface.getPropertyType(propName);
          if (propType instanceof FunctionType) {
            return (FunctionType) propType;
          }
        }
      }

      return null;
    }
}
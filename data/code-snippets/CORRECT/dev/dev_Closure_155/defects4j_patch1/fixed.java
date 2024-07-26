public class test {
    private boolean isLValue(Node n) {
      Node parent = n.getParent();
      return (parent.getType() == Token.INC
          || parent.getType() == Token.DEC
          || (NodeUtil.isAssignmentOp(parent)
          && parent.getFirstChild() == n));
    }
    private void doInlinesForScope(NodeTraversal t,
        Map<Var, ReferenceCollection> referenceMap) {

      boolean maybeModifiedArguments =
          maybeEscapedOrModifiedArguments(t.getScope(), referenceMap);
      for (Iterator<Var> it = t.getScope().getVars(); it.hasNext();) {
        Var v = it.next();

        ReferenceCollection referenceInfo = referenceMap.get(v);

        // referenceInfo will be null if we're in constants-only mode
        // and the variable is not a constant.
        if (referenceInfo == null || isVarInlineForbidden(v)) {
          // Never try to inline exported variables or variables that
          // were not collected or variables that have already been inlined.
          continue;
        } else if (isInlineableDeclaredConstant(v, referenceInfo)) {
          Reference init = referenceInfo.getInitializingReferenceForConstants();
          Node value = init.getAssignedValue();
          inlineDeclaredConstant(v, value, referenceInfo.references);
          staleVars.add(v);
        } else if (mode == Mode.CONSTANTS_ONLY) {
          // If we're in constants-only mode, don't run more aggressive
          // inlining heuristics. See InlineConstantsTest.
          continue;
        } else {
          inlineNonConstants(v, referenceInfo, maybeModifiedArguments);
        }
      }
    }
    private void inlineNonConstants(
        Var v, ReferenceCollection referenceInfo,
        boolean maybeModifiedArguments) {
      int refCount = referenceInfo.references.size();
      Reference declaration = referenceInfo.references.get(0);
      Reference init = referenceInfo.getInitializingReference();
      int firstRefAfterInit = (declaration == init) ? 2 : 3;

      if (refCount > 1 &&
          isImmutableAndWellDefinedVariable(v, referenceInfo)) {
        // if the variable is referenced more than once, we can only
        // inline it if it's immutable and never defined before referenced.
        Node value;
        if (init != null) {
          value = init.getAssignedValue();
        } else {
          // Create a new node for variable that is never initialized.
          Node srcLocation = declaration.getNameNode();
          value = NodeUtil.newUndefinedNode(srcLocation);
        }
        Preconditions.checkNotNull(value);
        inlineWellDefinedVariable(v, value, referenceInfo.references);
        staleVars.add(v);
      } else if (refCount == firstRefAfterInit) {
        // The variable likely only read once, try some more
        // complex inlining heuristics.
        Reference reference = referenceInfo.references.get(
            firstRefAfterInit - 1);
        if (canInline(declaration, init, reference)) {
          inline(v, declaration, init, reference);
          staleVars.add(v);
        }
      } else if (declaration != init && refCount == 2) {
        if (isValidDeclaration(declaration) && isValidInitialization(init)) {
          // The only reference is the initialization, remove the assignment and
          // the variable declaration.
          Node value = init.getAssignedValue();
          Preconditions.checkNotNull(value);
          inlineWellDefinedVariable(v, value, referenceInfo.references);
          staleVars.add(v);
        }
      }

      // If this variable was not inlined normally, check if we can
      // inline an alias of it. (If the variable was inlined, then the
      // reference data is out of sync. We're better off just waiting for
      // the next pass.)
      if (!maybeModifiedArguments &&
          !staleVars.contains(v) && referenceInfo.isWellDefined() &&
          referenceInfo.isAssignedOnceInLifetime()) {
        List<Reference> refs = referenceInfo.references;
        for (int i = 1 /* start from a read */; i < refs.size(); i++) {
          Node nameNode = refs.get(i).getNameNode();
          if (aliasCandidates.containsKey(nameNode)) {
            AliasCandidate candidate = aliasCandidates.get(nameNode);
            if (!staleVars.contains(candidate.alias) &&
                !isVarInlineForbidden(candidate.alias)) {
              Reference aliasInit;
              aliasInit = candidate.refInfo.getInitializingReference();
              Node value = aliasInit.getAssignedValue();
              Preconditions.checkNotNull(value);
              inlineWellDefinedVariable(candidate.alias,
                  value,
                  candidate.refInfo.references);
              staleVars.add(candidate.alias);
            }
          }
        }
      }
    }
    private boolean maybeEscapedOrModifiedArguments(
        Scope scope, Map<Var, ReferenceCollection> referenceMap) {
      if (scope.isLocal()) {
        Var arguments = scope.getArgumentsVar();
        ReferenceCollection refs = referenceMap.get(arguments);
        if (refs != null && !refs.references.isEmpty()) {
          for (Reference ref : refs.references) {
            Node refNode = ref.getNameNode();
            Node refParent = ref.getParent();
            // Any reference that is not a read of the arguments property
            // consider a escape of the arguments object.
            if (!(NodeUtil.isGet(refParent)
                && refNode == ref.getParent().getFirstChild()
                && !isLValue(refParent))) {
              return true;
            }
          }
        }
      }
      return false;
    }
  public void visit(NodeTraversal t, Node n, Node parent) {
    if (n.getType() == Token.NAME) {
      Var v;
      if (n.getString().equals("arguments")) {
        v = t.getScope().getArgumentsVar();
      } else {
        v = t.getScope().getVar(n.getString());
      }
      if (v != null && varFilter.apply(v)) {
        addReference(t, v,
            new Reference(n, parent, t, blockStack.peek()));
      }
    }

    if (isBlockBoundary(n, parent)) {
      blockStack.pop();
    }
  }
    Arguments(Scope scope) {
      super(
        false, // no inferred
        "arguments", // always arguments
        null,  // no declaration node
        // TODO(johnlenz): provide the type of "Arguments".
        null,  // no type info
        scope,
        -1,    // no variable index
        null,  // input,
        false, // not a define
        null   // no jsdoc
        );
    }
    @Override public boolean equals(Object other) {
      if (!(other instanceof Arguments)) {
        return false;
      }

      Arguments otherVar = (Arguments) other;
      return otherVar.scope.getRootNode() == scope.getRootNode();
    }
    @Override public int hashCode() {
      return System.identityHashCode(this);
    }
  public Var getArgumentsVar() {
    if (arguments == null) {
      arguments = new Arguments(this);
    }
    return arguments;
  }
}
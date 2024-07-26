public class test {
    public void exitScope(NodeTraversal t) {
      if (t.getScopeDepth() > 2) {
        findNamespaceShadows(t);
      }

      if (t.getScopeDepth() == 2) {
        renameNamespaceShadows(t);
        aliases.clear();
        forbiddenLocals.clear();
        transformation = null;
        hasNamespaceShadows = false;
      }
    }
    public void visit(NodeTraversal t, Node n, Node parent) {
      if (isCallToScopeMethod(n)) {
        validateScopeCall(t, n, n.getParent());
      }

      if (t.getScopeDepth() < 2) {
        return;
      }

      int type = n.getType();
      Var aliasVar = null;
      if (type == Token.NAME) {
        String name = n.getString();
        Var lexicalVar = t.getScope().getVar(n.getString());
        if (lexicalVar != null && lexicalVar == aliases.get(name)) {
          aliasVar = lexicalVar;
        }
      }

      // Validate the top-level of the goog.scope block.
      if (t.getScopeDepth() == 2) {
        if (aliasVar != null && NodeUtil.isLValue(n)) {
          if (aliasVar.getNode() == n) {
            aliasDefinitionsInOrder.add(n);

            // Return early, to ensure that we don't record a definition
            // twice.
            return;
          } else {
            report(t, n, GOOG_SCOPE_ALIAS_REDEFINED, n.getString());
          }
        }

        if (type == Token.RETURN) {
          report(t, n, GOOG_SCOPE_USES_RETURN);
        } else if (type == Token.THIS) {
          report(t, n, GOOG_SCOPE_REFERENCES_THIS);
        } else if (type == Token.THROW) {
          report(t, n, GOOG_SCOPE_USES_THROW);
        }
      }

      // Validate all descendent scopes of the goog.scope block.
      if (t.getScopeDepth() >= 2) {
        // Check if this name points to an alias.
        if (aliasVar != null) {
          // Note, to support the transitive case, it's important we don't
          // clone aliasedNode here.  For example,
          // var g = goog; var d = g.dom; d.createElement('DIV');
          // The node in aliasedNode (which is "g") will be replaced in the
          // changes pass above with "goog".  If we cloned here, we'd end up
          // with <code>g.dom.createElement('DIV')</code>.
          aliasUsages.add(new AliasedNode(aliasVar, n));
        }

        // When we inject declarations, we duplicate jsdoc. Make sure
        // we only process that jsdoc once.
        JSDocInfo info = n.getJSDocInfo();
        if (info != null) {
          for (Node node : info.getTypeNodes()) {
            fixTypeNode(node);
          }
        }

        // TODO(robbyw): Error for goog.scope not at root.
      }
    }
    private void findAliases(NodeTraversal t) {
      Scope scope = t.getScope();
      for (Var v : scope.getVarIterable()) {
        Node n = v.getNode();
        Node parent = n.getParent();
        boolean isVar = parent.isVar();
        boolean isFunctionDecl = NodeUtil.isFunctionDeclaration(parent);
        if (isVar && n.getFirstChild() != null && n.getFirstChild().isQualifiedName()) {
          recordAlias(v);
        } else if (v.isBleedingFunction()) {
          // Bleeding functions already get a BAD_PARAMETERS error, so just
          // do nothing.
        } else if (parent.getType() == Token.LP) {
          // Parameters of the scope function also get a BAD_PARAMETERS
          // error.
        } else if (isVar || isFunctionDecl) {
          boolean isHoisted = NodeUtil.isHoistedFunctionDeclaration(parent);
          Node grandparent = parent.getParent();
          Node value = v.getInitialValue() != null ?
              v.getInitialValue() :
              null;
          Node varNode = null;

          String name = n.getString();
          int nameCount = scopedAliasNames.count(name);
          scopedAliasNames.add(name);
          String globalName =
              "$jscomp.scope." + name + (nameCount == 0 ? "" : ("$" + nameCount));

          compiler.ensureLibraryInjected("base");

          // First, we need to free up the function expression (EXPR)
          // to be used in another expression.
          if (isFunctionDecl) {
            // Replace "function NAME() { ... }" with "var NAME;".
            Node existingName = v.getNameNode();

            // We can't keep the local name on the function expression,
            // because IE is buggy and will leak the name into the global
            // scope. This is covered in more detail here:
            // http://wiki.ecmascript.org/lib/exe/fetch.php?id=resources:resources&cache=cache&media=resources:jscriptdeviationsfromes3.pdf
            //
            // This will only cause problems if this is a hoisted, recursive
            // function, and the programmer is using the hoisting.
            Node newName = IR.name("").useSourceInfoFrom(existingName);
            value.replaceChild(existingName, newName);

            varNode = IR.var(existingName).useSourceInfoFrom(existingName);
            grandparent.replaceChild(parent, varNode);
          } else {
            if (value != null) {
              // If this is a VAR, we can just detach the expression and
              // the tree will still be valid.
              value.detachFromParent();
            }
            varNode = parent;
          }

          // Add $jscomp.scope.name = EXPR;
          // Make sure we copy over all the jsdoc and debug info.
          if (value != null || v.getJSDocInfo() != null) {
            Node newDecl = NodeUtil.newQualifiedNameNodeDeclaration(
                compiler.getCodingConvention(),
                globalName,
                value,
                v.getJSDocInfo())
                .useSourceInfoIfMissingFromForTree(n);
            NodeUtil.setDebugInformation(
                newDecl.getFirstChild().getFirstChild(), n, name);

            if (isHoisted) {
              grandparent.addChildToFront(newDecl);
            } else {
              grandparent.addChildBefore(newDecl, varNode);
            }
          }

          // Rewrite "var name = EXPR;" to "var name = $jscomp.scope.name;"
          v.getNameNode().addChildToFront(
              NodeUtil.newQualifiedNameNode(
                  compiler.getCodingConvention(), globalName, n, name));

          recordAlias(v);
        } else {
          // Do not other kinds of local symbols, like catch params.
          report(t, n, GOOG_SCOPE_NON_ALIAS_LOCAL, n.getString());
        }
      }
    }
}
public class test {
  private void parse(AbstractCompiler compiler) {
    try {
      ParserRunner.ParseResult result = ParserRunner.parse(sourceFile, sourceFile.getCode(),
          compiler.getParserConfig(),
          compiler.getDefaultErrorReporter(),
          logger_);
      root = result.ast;
      compiler.setOldParseTree(sourceFile.getName(), result.oldAst);
    } catch (IOException e) {
      compiler.report(
          JSError.make(AbstractCompiler.READ_ERROR, sourceFile.getName()));
    }


    if (root == null || compiler.hasHaltingErrors()) {
        // Most passes try to report as many errors as possible,
        // so there may already be errors. We only care if there were
        // errors in the code we just parsed.
      // There was a parse error or IOException, so use a dummy block.
      root = IR.script();
    } else {
      compiler.prepareAst(root);
    }

    // Set the source name so that the compiler passes can track
    // the source file and module.
    root.setStaticSourceFile(sourceFile);
  }
  public static Node newQualifiedNameNodeDeclaration(
      CodingConvention convention, String name, Node value, JSDocInfo info) {
    Node result;
    Node nameNode = newQualifiedNameNode(convention, name);
    if (nameNode.isName()) {
      result = IR.var(nameNode, value);
      result.setJSDocInfo(info);
    } else {
      result = IR.exprResult(IR.assign(nameNode, value));
      result.getFirstChild().setJSDocInfo(info);
    }
    return result;
  }
    private void findAliases(NodeTraversal t) {
      Scope scope = t.getScope();
      for (Var v : scope.getVarIterable()) {
        Node n = v.getNode();
        Node parent = n.getParent();
        boolean isVarAssign = parent.isVar() && n.hasChildren();
        if (isVarAssign && n.getFirstChild().isQualifiedName()) {
          recordAlias(v);
        } else if (v.isBleedingFunction()) {
          // Bleeding functions already get a BAD_PARAMETERS error, so just
          // do nothing.
        } else if (parent.getType() == Token.LP) {
          // Parameters of the scope function also get a BAD_PARAMETERS
          // error.
        } else if (isVarAssign) {
          Node value = v.getInitialValue().detachFromParent();
          String name = n.getString();
          int nameCount = scopedAliasNames.count(name);
          scopedAliasNames.add(name);
          String globalName =
              "$jscomp.scope." + name + (nameCount == 0 ? "" : ("$" + nameCount));

          compiler.ensureLibraryInjected("base");

          // Add $jscomp.scope.name = EXPR;
          // Make sure we copy over all the jsdoc and debug info.
            Node newDecl = NodeUtil.newQualifiedNameNodeDeclaration(
                compiler.getCodingConvention(),
                globalName,
                value,
                v.getJSDocInfo())
                .useSourceInfoIfMissingFromForTree(n);
            NodeUtil.setDebugInformation(
                newDecl.getFirstChild().getFirstChild(), n, name);
            parent.getParent().addChildBefore(newDecl, parent);

          // Rewrite "var name = EXPR;" to "var name = $jscomp.scope.name;"
          v.getNameNode().addChildToFront(
              NodeUtil.newQualifiedNameNode(
                  compiler.getCodingConvention(), globalName, n, name));

          recordAlias(v);
        } else {
          // Do not allow hoisted functions or other kinds of local symbols.
          report(t, n, GOOG_SCOPE_NON_ALIAS_LOCAL, n.getString());
        }
      }
    }
}
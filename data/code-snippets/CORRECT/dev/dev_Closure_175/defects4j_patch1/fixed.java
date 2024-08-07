public class test {
  private CanInlineResult canInlineReferenceDirectly(
      Node callNode, Node fnNode, Set<String> namesToAlias) {
    if (!isDirectCallNodeReplacementPossible(fnNode)) {
      return CanInlineResult.NO;
    }

    // CALL NODE: [ NAME, ARG1, ARG2, ... ]
    Node cArg = callNode.getFirstChild().getNext();

    // Functions called via 'call' and 'apply' have a this-object as
    // the first parameter, but this is not part of the called function's
    // parameter list.
    if (!callNode.getFirstChild().isName()) {
      if (NodeUtil.isFunctionObjectCall(callNode)) {
        // TODO(johnlenz): Support replace this with a value.
        if (cArg == null || !cArg.isThis()) {
          return CanInlineResult.NO;
        }
        cArg = cArg.getNext();
      } else {
        // ".apply" call should be filtered before this.
        Preconditions.checkState(!NodeUtil.isFunctionObjectApply(callNode));
      }
    }

    Map<String, Node> args =
        FunctionArgumentInjector.getFunctionCallParameterMap(
            fnNode, callNode, this.throwawayNameSupplier);
    boolean hasArgs = !args.isEmpty();
    if (hasArgs) {
      // Limit the inlining
      Set<String> allNamesToAlias = Sets.newHashSet(namesToAlias);
      FunctionArgumentInjector.maybeAddTempsForCallArguments(
          fnNode, args, allNamesToAlias, compiler.getCodingConvention());
      if (!allNamesToAlias.isEmpty()) {
        return CanInlineResult.NO;
      }
    }

    return CanInlineResult.YES;
  }
  CanInlineResult canInlineReferenceToFunction(NodeTraversal t,
      Node callNode, Node fnNode, Set<String> needAliases,
      InliningMode mode, boolean referencesThis, boolean containsFunctions) {
    // TODO(johnlenz): This function takes too many parameter, without
    // context.  Modify the API to take a structure describing the function.

    // Allow direct function calls or "fn.call" style calls.
    if (!isSupportedCallType(callNode)) {
      return CanInlineResult.NO;
    }

    // Limit where functions that contain functions can be inline.  Introducing
    // an inner function into another function can capture a variable and cause
    // a memory leak.  This isn't a problem in the global scope as those values
    // last until explicitly cleared.
    if (containsFunctions) {
      if (!assumeMinimumCapture && !t.inGlobalScope()) {
        // TODO(johnlenz): Allow inlining into any scope without local names or
        // inner functions.
        return CanInlineResult.NO;
      } else if (NodeUtil.isWithinLoop(callNode)) {
        // An inner closure maybe relying on a local value holding a value for a
        // single iteration through a loop.
        return CanInlineResult.NO;
      }
    }

    // TODO(johnlenz): Add support for 'apply'
    if (referencesThis && !NodeUtil.isFunctionObjectCall(callNode)) {
      // TODO(johnlenz): Allow 'this' references to be replaced with a
      // global 'this' object.
      return CanInlineResult.NO;
    }

    if (mode == InliningMode.DIRECT) {
      return canInlineReferenceDirectly(callNode, fnNode, needAliases);
    } else {
      return canInlineReferenceAsStatementBlock(
          t, callNode, fnNode, needAliases);
    }
  }
}
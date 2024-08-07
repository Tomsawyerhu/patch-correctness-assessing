public class test {
  protected List<PassFactory> getChecks() {
    List<PassFactory> checks = Lists.newArrayList();

    if (options.closurePass) {
      checks.add(closureGoogScopeAliases);
    }

    if (options.nameAnonymousFunctionsOnly) {
      if (options.anonymousFunctionNaming ==
          AnonymousFunctionNamingPolicy.MAPPED) {
        checks.add(nameMappedAnonymousFunctions);
      } else if (options.anonymousFunctionNaming ==
          AnonymousFunctionNamingPolicy.UNMAPPED) {
        checks.add(nameUnmappedAnonymousFunctions);
      }
      return checks;
    }

    if (options.checkSuspiciousCode ||
        options.enables(DiagnosticGroups.GLOBAL_THIS)) {
      checks.add(suspiciousCode);
    }

    if (options.checkControlStructures)  {
      checks.add(checkControlStructures);
    }

    if (options.checkRequires.isOn()) {
      checks.add(checkRequires);
    }

    if (options.checkProvides.isOn()) {
      checks.add(checkProvides);
    }

    // The following passes are more like "preprocessor" passes.
    // It's important that they run before most checking passes.
    // Perhaps this method should be renamed?
    if (options.generateExports) {
      checks.add(generateExports);
    }

    if (options.exportTestFunctions) {
      checks.add(exportTestFunctions);
    }

    if (options.closurePass) {
      checks.add(closurePrimitives.makeOneTimePass());
    }

    if (options.closurePass && options.checkMissingGetCssNameLevel.isOn()) {
      checks.add(closureCheckGetCssName);
    }

    if (options.syntheticBlockStartMarker != null) {
      // This pass must run before the first fold constants pass.
      checks.add(createSyntheticBlocks);
    }

    checks.add(checkVars);
    if (options.computeFunctionSideEffects) {
      checks.add(checkRegExp);
    }

    if (options.checkShadowVars.isOn()) {
      checks.add(checkShadowVars);
    }

    if (options.aggressiveVarCheck.isOn()) {
      checks.add(checkVariableReferences);
    }

    // This pass should run before types are assigned.
    if (options.processObjectPropertyString) {
      checks.add(objectPropertyStringPreprocess);
    }

    if (options.checkTypes || options.inferTypes) {
      checks.add(resolveTypes.makeOneTimePass());
      checks.add(inferTypes.makeOneTimePass());
      if (options.checkTypes) {
        checks.add(checkTypes.makeOneTimePass());
      } else {
        checks.add(inferJsDocInfo.makeOneTimePass());
      }
    }

    if (options.checkUnreachableCode.isOn() ||
        (options.checkTypes && options.checkMissingReturn.isOn())) {
      checks.add(checkControlFlow);
    }

    // CheckAccessControls only works if check types is on.
    if (options.checkTypes &&
        (options.enables(DiagnosticGroups.ACCESS_CONTROLS)
         || options.enables(DiagnosticGroups.CONSTANT_PROPERTY))) {
      checks.add(checkAccessControls);
    }

    if (options.checkGlobalNamesLevel.isOn()) {
      checks.add(checkGlobalNames);
    }

    checks.add(checkStrictMode);

    // Replace 'goog.getCssName' before processing defines but after the
    // other checks have been done.
    if (options.closurePass) {
      checks.add(closureReplaceGetCssName);
    }

    // i18n
    // If you want to customize the compiler to use a different i18n pass,
    // you can create a PassConfig that calls replacePassFactory
    // to replace this.
    checks.add(options.messageBundle != null ?
        replaceMessages : createEmptyPass("replaceMessages"));

    if (options.getTweakProcessing().isOn()) {
      checks.add(processTweaks);
    }

    // Defines in code always need to be processed.
    checks.add(processDefines);

    if (options.instrumentationTemplate != null ||
        options.recordFunctionInformation) {
      checks.add(computeFunctionNames);
    }

    if (options.nameReferenceGraphPath != null &&
        !options.nameReferenceGraphPath.isEmpty()) {
      checks.add(printNameReferenceGraph);
    }

    if (options.nameReferenceReportPath != null &&
        !options.nameReferenceReportPath.isEmpty()) {
      checks.add(printNameReferenceReport);
    }

    assertAllOneTimePasses(checks);
    return checks;
  }
}
public class test {
  private static void applySafeCompilationOptions(CompilerOptions options) {
    // Does not call applyBasicCompilationOptions(options) because the call to
    // skipAllCompilerPasses() cannot be easily undone.
    options.closurePass = true;
    options.variableRenaming = VariableRenamingPolicy.LOCAL;
    options.inlineLocalVariables = true;
    options.checkGlobalThisLevel = CheckLevel.OFF;
    options.foldConstants = true;
    options.removeConstantExpressions = true;
    options.coalesceVariableNames = true;
    options.deadAssignmentElimination = true;
    options.extractPrototypeMemberDeclarations = true;
    options.collapseVariableDeclarations = true;
    options.convertToDottedProperties = true;
    options.labelRenaming = true;
    options.removeDeadCode = true;
    options.optimizeArgumentsArray = true;
    options.removeUnusedVars = true;
    options.removeUnusedVarsInGlobalScope = false;

    // Allows annotations that are not standard.
    options.setWarningLevel(DiagnosticGroups.NON_STANDARD_JSDOC,
        CheckLevel.OFF);
  }
}
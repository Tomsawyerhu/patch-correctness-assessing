public class test {
    private void emitOptionalModuleExportsOverride(Node script,
        String moduleName) {

      Node moduleExportsProp = IR.getprop(IR.name(moduleName),
          IR.string("module$exports"));
      script.addChildToBack(IR.ifNode(
          moduleExportsProp,
          IR.block(IR.exprResult(IR.assign(IR.name(moduleName),
              moduleExportsProp.cloneTree())))).copyInformationFromForTree(
          script));
    }
    private void visitModuleExports(Node prop) {
      String moduleName = guessCJSModuleName(prop.getSourceFileName());
      Node module = prop.getChildAtIndex(0);
      module.putProp(Node.ORIGINALNAME_PROP, "module");
      module.setString(moduleName);
      Node exports = prop.getChildAtIndex(1);
      exports.putProp(Node.ORIGINALNAME_PROP, "exports");
      exports.setString("module$exports");
    }
}
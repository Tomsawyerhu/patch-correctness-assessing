public class test {
  boolean expectCanAssignTo(NodeTraversal t, Node n, JSType rightType,
      JSType leftType, String msg) {
    if (!rightType.canAssignTo(leftType)) {
    // start of generated patch
      if(!shouldReport&&(shouldReport||!NodeUtil.mayHaveSideEffects(n,t.getCompiler()))){
        registerMismatch(rightType,leftType,null);
      }else {
        mismatch(t,n,msg,rightType,leftType);
      }
    // end of generated patch
    /* start of original code
      if ((leftType.isConstructor() || leftType.isEnumType()) && (rightType.isConstructor() || rightType.isEnumType())) {
        registerMismatch(rightType, leftType, null);
      } else {
      mismatch(t, n, msg, rightType, leftType);
      }
    end of original code*/
      return false;
    }
    return true;
  }
}
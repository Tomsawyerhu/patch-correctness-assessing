public class test {
  static boolean isValidDefineValue(Node val, Set<String> defines) {
    switch (val.getType()) {
      case Token.STRING:
      case Token.NUMBER:
      case Token.TRUE:
      case Token.FALSE:
        return true;

      // Binary operators are only valid if both children are valid.
      case Token.ADD:
      case Token.BITAND:
      case Token.BITNOT:
      case Token.BITOR:
      case Token.BITXOR:
      case Token.DIV:
      case Token.EQ:
      case Token.GE:
      case Token.GT:
      case Token.LE:
      case Token.LSH:
      case Token.LT:
      case Token.MOD:
      case Token.MUL:
      case Token.NE:
      case Token.RSH:
      case Token.SHEQ:
      case Token.SHNE:
      case Token.SUB:
      case Token.URSH:
        return isValidDefineValue(val.getFirstChild(), defines)
            && isValidDefineValue(val.getLastChild(), defines);

      // Uniary operators are valid if the child is valid.
      case Token.NOT:
      case Token.NEG:
      case Token.POS:
        return isValidDefineValue(val.getFirstChild(), defines);

      // Names are valid if and only if they are defines themselves.
      case Token.NAME:
      case Token.GETPROP:
        if (val.isQualifiedName()) {
          return defines.contains(val.getQualifiedName());
        }
    }
    return false;
  }
}
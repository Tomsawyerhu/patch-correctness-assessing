public class test {
        public String getDescription() {
            if (_desc == null) {
                StringBuilder sb = new StringBuilder();

                if (_from == null) { // can this ever occur?
                    sb.append("UNKNOWN");
                } else {
                    Class<?> cls = (_from instanceof Class<?>) ? (Class<?>)_from : _from.getClass();
                    // Hmmh. Although Class.getName() is mostly ok, it does look
                    // butt-ugly for arrays.
                    // 06-Oct-2016, tatu: as per [databind#1403], `getSimpleName()` not so good
                    //   as it drops enclosing class. So let's try bit different approach
                    String pkgName = ClassUtil.getPackageName(cls);
                    if (pkgName != null) {
                        sb.append(pkgName);
                        sb.append('.');
                    }
                    sb.append(cls.getSimpleName());
                }
                sb.append('[');
                if (_fieldName != null) {
                    sb.append('"');
                    sb.append(_fieldName);
                    sb.append('"');
                } else if (_index >= 0) {
                    sb.append(_index);
                } else {
                    sb.append('?');
                }
                sb.append(']');
                _desc = sb.toString();
            }
            return _desc;
        }
}
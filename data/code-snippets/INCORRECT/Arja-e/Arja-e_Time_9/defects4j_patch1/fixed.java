public class test {
    public static DateTimeZone forOffsetMillis(int millisOffset) {
        String id = printOffset(millisOffset);
        if (id.startsWith("+") || id.startsWith("-")) {
			int offset = parseOffset(id);
			if (offset == 0L) {
				return DateTimeZone.UTC;
			} else {
				id = printOffset(offset);
				return fixedOffsetZone(id, offset);
			}
		}
        return fixedOffsetZone(id, millisOffset);
    }
}
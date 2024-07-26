public class test {
    protected Date parseAsISO8601(String dateStr, ParsePosition pos)
    {
        /* 21-May-2009, tatu: DateFormat has very strict handling of
         * timezone  modifiers for ISO-8601. So we need to do some scrubbing.
         */

        /* First: do we have "zulu" format ('Z' == "GMT")? If yes, that's
         * quite simple because we already set date format timezone to be
         * GMT, and hence can just strip out 'Z' altogether
         */
        int len = dateStr.length();
        char c = dateStr.charAt(len-1);
        DateFormat df;

        // [JACKSON-200]: need to support "plain" date...
        if (len <= 10 && Character.isDigit(c)) {
            df = _formatPlain;
            if (df == null) {
                df = _formatPlain = _cloneFormat(DATE_FORMAT_PLAIN, DATE_FORMAT_STR_PLAIN, _timezone, _locale);
            }
        } else if (c == 'Z') {
            df = _formatISO8601_z;
            if (df == null) {
                df = _formatISO8601_z = _cloneFormat(DATE_FORMAT_ISO8601_Z, DATE_FORMAT_STR_ISO8601_Z, _timezone, _locale);
            }
            // [JACKSON-334]: may be missing milliseconds... if so, add
            if (dateStr.charAt(len-4) == ':') {
                StringBuilder sb = new StringBuilder(dateStr);
                sb.insert(len-1, ".000");
                dateStr = sb.toString();
            }
        } else {
            // Let's see if we have timezone indicator or not...
            if (hasTimeZone(dateStr)) {
                c = dateStr.charAt(len-3);
                if (c == ':') { // remove optional colon
                    // remove colon
                    StringBuilder sb = new StringBuilder(dateStr);
                    sb.delete(len-3, len-2);
                    dateStr = sb.toString();
                } else if (c == '+' || c == '-') { // missing minutes
                    // let's just append '00'
                    dateStr += "00";
                }
                // Milliseconds partial or missing; and even seconds are optional
                len = dateStr.length();
                // remove 'T', '+'/'-' and 4-digit timezone-offset
                int timeLen = len - dateStr.lastIndexOf('T') - 6;
                if (timeLen < 12) { // 8 for hh:mm:ss, 4 for .sss
                    int offset = len - 5; // insertion offset, before tz-offset
                    StringBuilder sb = new StringBuilder(dateStr);
                    switch (timeLen) {
                    case 11:
                        sb.insert(offset, '0'); break;
                    case 10:
                        sb.insert(offset, "00"); break;
                    case 9: // is this legal? (just second fraction marker)
                        sb.insert(offset, "000"); break;
                    case 8:
                        sb.insert(offset, ".000"); break;
                    case 7: // not legal to have single-digit second
                        break;
                    case 6: // probably not legal, but let's allow
                        sb.insert(offset, "00.000");
                    case 5: // is legal to omit seconds
                        sb.insert(offset, ":00.000");
                    }
                    dateStr = sb.toString();
                }
                df = _formatISO8601;
                if (_formatISO8601 == null) {
                    df = _formatISO8601 = _cloneFormat(DATE_FORMAT_ISO8601, DATE_FORMAT_STR_ISO8601, _timezone, _locale);
                }
            } else {
                // If not, plain date. Easiest to just patch 'Z' in the end?
                StringBuilder sb = new StringBuilder(dateStr);
                // And possible also millisecond part if missing
                int timeLen = len - dateStr.lastIndexOf('T') - 1;
                if (timeLen < 12) { // missing, or partial
                    switch (timeLen) {
                    case 11: sb.append('0');
                    case 10: sb.append('0');
                    case 9: sb.append('0');
                        break;
                    default:
                        sb.append(".000");
                    }
                }
                sb.append('Z');
                dateStr = sb.toString();
                df = _formatISO8601_z;
                if (df == null) {
                    df = _formatISO8601_z = _cloneFormat(DATE_FORMAT_ISO8601_Z, DATE_FORMAT_STR_ISO8601_Z,
                            _timezone, _locale);
                }
            }
        }
        return df.parse(dateStr, pos);
    }
}
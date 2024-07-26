public class test {
    protected void _format(TimeZone tz, Locale loc, Date date,
            StringBuffer buffer)
    {
        Calendar cal = _getCalendar(tz);
        cal.setTime(date);
        // [databind#2167]: handle range beyond [1, 9999]
        final int year = cal.get(Calendar.YEAR);

        // Assuming GregorianCalendar, special handling needed for BCE (aka BC)
        if (cal.get(Calendar.ERA) == GregorianCalendar.BC) {
            _formatBCEYear(buffer, year);
        } else {
            if (year > 9999) {
                // 22-Nov-2018, tatu: Handling beyond 4-digits is not well specified wrt ISO-8601, but
                //   it seems that plus prefix IS mandated. Padding is an open question, but since agreeement
                //   for max length would be needed, we ewould need to limit to arbitrary length
                //   like five digits (erroring out if beyond or padding to that as minimum).
                //   Instead, let's just print number out as is and let decoder try to make sense of it.
                buffer.append('+');
            }
            pad4(buffer, year);
        }
        buffer.append('-');
        pad2(buffer, cal.get(Calendar.MONTH) + 1);
        buffer.append('-');
        pad2(buffer, cal.get(Calendar.DAY_OF_MONTH));
        buffer.append('T');
        pad2(buffer, cal.get(Calendar.HOUR_OF_DAY));
        buffer.append(':');
        pad2(buffer, cal.get(Calendar.MINUTE));
        buffer.append(':');
        pad2(buffer, cal.get(Calendar.SECOND));
        buffer.append('.');
        pad3(buffer, cal.get(Calendar.MILLISECOND));

        int offset = tz.getOffset(cal.getTimeInMillis());
        if (offset != 0) {
            int hours = Math.abs((offset / (60 * 1000)) / 60);
            int minutes = Math.abs((offset / (60 * 1000)) % 60);
            buffer.append(offset < 0 ? '-' : '+');
            pad2(buffer, hours);
            if( _tzSerializedWithColon ) {
            		buffer.append(':');
            }
            pad2(buffer, minutes);
        } else {
            // 24-Jun-2017, tatu: While `Z` would be conveniently short, older specs
            //   mandate use of full `+0000`
//            formatted.append('Z');
	        	if( _tzSerializedWithColon ) {
	            buffer.append("+00:00");
	        	}
	        	else {
	        		buffer.append("+0000");
	        	}
        }
    }
    protected void _formatBCEYear(StringBuffer buffer, int bceYearNoSign) {
        // Ok. First of all, BCE 1 output (given as value `1` in era BCE) needs to become
        // "+0000", but rest (from `2` up, in that era) need minus sign.
        if (bceYearNoSign == 1) {
            buffer.append("+0000");
            return;
        }
        final int isoYear = bceYearNoSign - 1;
        buffer.append('-');
        // as with CE, 4 digit variant needs padding; beyond that not (although that part is
        // open to debate, needs agreement with receiver)
        // But `pad4()` deals with "big" numbers now so:
        pad4(buffer, isoYear);
    }
    private static void pad4(StringBuffer buffer, int value) {
        int h = value / 100;
        if (h == 0) {
            buffer.append('0').append('0');
        } else {
            if (h > 99) { // [databind#2167]: handle above 9999 correctly
                buffer.append(h);
            } else {
                pad2(buffer, h);
            }
            value -= (100 * h);
        }
        pad2(buffer, value);
    }
}
public class test {
        TimeZoneNameRule(TimeZone timeZone, Locale locale, int style) {
            mLocale = locale;
            mStyle = style;
            zone = timeZone;
            
            mStandard = getTimeZoneDisplay(timeZone, false, style, locale);
            mDaylight = getTimeZoneDisplay(timeZone, true, style, locale);
        }
        public void appendTo(StringBuffer buffer, Calendar calendar) {
            if (zone.useDaylightTime()
                    && calendar.get(Calendar.DST_OFFSET) != 0) {
                buffer.append(getTimeZoneDisplay(zone, true, mStyle, mLocale));
            } else {
                buffer.append(getTimeZoneDisplay(zone, false, mStyle, mLocale));
            }
        }
}
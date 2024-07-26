public class test {
    private final static DateFormat _cloneFormat(DateFormat df, String format,
            TimeZone tz, Locale loc, Boolean lenient)
    {
        if (!loc.equals(DEFAULT_LOCALE)) {
            df = new SimpleDateFormat(format, loc);
            df.setTimeZone((tz == null) ? DEFAULT_TIMEZONE : tz);
        } else {
            df = (DateFormat) df.clone();
            if (tz != null) {
                df.setTimeZone(tz);
            }
        }
        if (lenient != null) {
            df.setLenient(lenient.booleanValue());
        }
        return df;
    }
    public void setLenient(boolean enabled) {
        Boolean newValue = enabled;
        if (_lenient != newValue) {
            _lenient = newValue;
            // and since leniency settings may have been used:
            _clearFormats();
        }
    }
}
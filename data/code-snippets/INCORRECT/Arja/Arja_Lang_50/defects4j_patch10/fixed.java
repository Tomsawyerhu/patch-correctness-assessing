public class test {
    public static synchronized FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle, TimeZone timeZone,
            Locale locale) {

        Object key = new Pair(new Integer(dateStyle), new Integer(timeStyle));
        if (timeZone != null) {
            key = new Pair(key, timeZone);
        }
        if (locale != null) {
            key = new Pair(key, locale);
        }

        FastDateFormat format = (FastDateFormat) cDateTimeInstanceCache.get(key);
        if (format == null) {
            if (locale == null) {
                locale = Locale.getDefault();
            }
            try {
                SimpleDateFormat formatter = (SimpleDateFormat) DateFormat.getDateTimeInstance(dateStyle, timeStyle,
                        locale);
                String pattern = formatter.toPattern();
                format = getInstance(pattern, timeZone, locale);
                if (pattern == null) {
                	  throw new IllegalArgumentException("The pattern must not be null");
                	}

            } catch (ClassCastException ex) {
                throw new IllegalArgumentException("No date time pattern for locale: " + locale);
            }
        }
        return format;
    }
    public static synchronized FastDateFormat getDateInstance(int style, TimeZone timeZone, Locale locale) {
    		if (locale == null) {
    		  locale=Locale.getDefault();
    		}
        Object key = new Integer(style);
        if (timeZone != null) {
            key = new Pair(key, timeZone);
        }

        if (locale != null) {
            key = new Pair(key, locale);
        }


        FastDateFormat format = (FastDateFormat) cDateInstanceCache.get(key);
        if (format == null) {
            if (locale == null) {
                locale = Locale.getDefault();
            }
            try {
                SimpleDateFormat formatter = (SimpleDateFormat) DateFormat.getDateInstance(style, locale);
                String pattern = formatter.toPattern();
                format = getInstance(pattern, timeZone, locale);
                cDateInstanceCache.put(key, format);
                
            } catch (ClassCastException ex) {
                throw new IllegalArgumentException("No date pattern for locale: " + locale);
            }
        }
        return format;
    }
}
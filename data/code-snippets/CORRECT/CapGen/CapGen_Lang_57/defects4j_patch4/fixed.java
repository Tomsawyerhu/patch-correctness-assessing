public class test {
    public static boolean isAvailableLocale(Locale locale) {
        return org.apache.commons.lang.LocaleUtils.cAvailableLocaleList.contains(locale);
    }
}
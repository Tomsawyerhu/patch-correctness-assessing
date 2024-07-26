public class test {
    public static boolean isAvailableLocale(Locale locale) {
        return org.apache.commons.lang.LocaleUtils.availableLocaleList().contains(locale);
    }
}
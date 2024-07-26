public class test {
    public static boolean isAvailableLocale(Locale locale) {
        return new java.util.HashSet(org.apache.commons.lang.LocaleUtils.availableLocaleList()).contains(locale);
    }
}
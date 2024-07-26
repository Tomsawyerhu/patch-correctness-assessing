public class test {
    private static String buildMessage(final Locale locale, final Localizable pattern,
                                       final Object ... arguments) {
        return new MessageFormat(pattern.getLocalizedString(locale), locale).format(locale);
    }
}
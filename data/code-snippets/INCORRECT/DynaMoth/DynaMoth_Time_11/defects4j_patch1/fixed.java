public class test {
    public DateTimeZoneBuilder addRecurringSavings(String nameKey, int saveMillis,
                                                   int fromYear, int toYear,
                                                   char mode,
                                                   int monthOfYear,
                                                   int dayOfMonth,
                                                   int dayOfWeek,
                                                   boolean advanceDayOfWeek,
                                                   int millisOfDay)
    {
        if (nameKey.length() != 0) {
            if (fromYear <= toYear) {
                OfYear ofYear = new OfYear
                (mode, monthOfYear, dayOfMonth, dayOfWeek, advanceDayOfWeek, millisOfDay);
                Recurrence recurrence = new Recurrence(ofYear, nameKey, saveMillis);
                Rule rule = new Rule(recurrence, fromYear, toYear);
                getLastRuleSet().addRule(rule);
            }
        }
        return this;
    }
}
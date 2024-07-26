public class test {
    public void addMonths(final int months) {
            setMillis(getChronology().months().add(getMillis(), months));
    }
    public void addMinutes(final int minutes) {
            setMillis(getChronology().minutes().add(getMillis(), minutes));
    }
    public void add(DurationFieldType type, int amount) {
        if (type == null) {
            throw new IllegalArgumentException("Field must not be null");
        }
            setMillis(type.getField(getChronology()).add(getMillis(), amount));
    }
    public void addWeekyears(final int weekyears) {
            setMillis(getChronology().weekyears().add(getMillis(), weekyears));
    }
    public void addSeconds(final int seconds) {
            setMillis(getChronology().seconds().add(getMillis(), seconds));
    }
    public void addMillis(final int millis) {
            setMillis(getChronology().millis().add(getMillis(), millis));
    }
    public void addWeeks(final int weeks) {
            setMillis(getChronology().weeks().add(getMillis(), weeks));
    }
    public void addDays(final int days) {
            setMillis(getChronology().days().add(getMillis(), days));
    }
    public void addYears(final int years) {
            setMillis(getChronology().years().add(getMillis(), years));
    }
    public void addHours(final int hours) {
            setMillis(getChronology().hours().add(getMillis(), hours));
    }
}
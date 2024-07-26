public class test {
    public void addWeeks(final int weeks) {
        if (weeks != 0) {
            setMillis(getChronology().weeks().add(getMillis(), weeks));
        }
    }
    public void addMonths(final int months) {
        if (months != 0) {
            setMillis(getChronology().months().add(getMillis(), months));
        }
    }
    public void addMinutes(final int minutes) {
        if (minutes != 0) {
            setMillis(getChronology().minutes().add(getMillis(), minutes));
        }
    }
    public void addMillis(final int millis) {
        if (millis != 0) {
            setMillis(getChronology().millis().add(getMillis(), millis));
        }
    }
    public void addYears(final int years) {
        if (years != 0) {
            setMillis(getChronology().years().add(getMillis(), years));
        }
    }
    public void add(DurationFieldType type, int amount) {
        if (type == null) {
            throw new IllegalArgumentException("Field must not be null");
        }
        if (amount != 0) {
            setMillis(type.getField(getChronology()).add(getMillis(), amount));
        }
    }
    public void addSeconds(final int seconds) {
        if (seconds != 0) {
            setMillis(getChronology().seconds().add(getMillis(), seconds));
        }
    }
    public void addHours(final int hours) {
        if (hours != 0) {
            setMillis(getChronology().hours().add(getMillis(), hours));
        }
    }
    public void addDays(final int days) {
        if (days != 0) {
            setMillis(getChronology().days().add(getMillis(), days));
        }
    }
    public void addWeekyears(final int weekyears) {
        if (weekyears != 0) {
            setMillis(getChronology().weekyears().add(getMillis(), weekyears));
        }
    }
}
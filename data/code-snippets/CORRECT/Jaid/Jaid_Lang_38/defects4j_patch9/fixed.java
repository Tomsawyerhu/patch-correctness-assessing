public class test {
    public StringBuffer format(Calendar calendar, StringBuffer buf) {
        if (mTimeZoneForced) {
            calendar.getTimeInMillis();
            calendar = (Calendar) calendar.clone();
            calendar.setTimeZone(mTimeZone);
        }
        return applyRules(calendar, buf);
    }
}
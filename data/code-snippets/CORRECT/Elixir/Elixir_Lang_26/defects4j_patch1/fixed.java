public class test {
    public String format(Date date) {
        Calendar c = new GregorianCalendar(mTimeZone, getLocale());
        c.setTime(date);
        return applyRules(c, new StringBuffer(mMaxLengthEstimate)).toString();
    }
}
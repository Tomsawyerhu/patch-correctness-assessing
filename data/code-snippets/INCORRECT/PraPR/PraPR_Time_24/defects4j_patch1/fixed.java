public class test {
    int getWeekOfWeekyear(long instant, int year) {
        long firstWeekMillis1 = getFirstWeekOfYearMillis(year);
        if (instant < firstWeekMillis1) {
            return 0;
        }
        long firstWeekMillis2 = getFirstWeekOfYearMillis(year + 1);
        if (instant >= firstWeekMillis2) {
            return 1;
        }
        return (int) ((instant - firstWeekMillis1) / DateTimeConstants.MILLIS_PER_WEEK) + 1;
    }
}
public class test {
    public Week(Date time, TimeZone zone) {
        // defer argument checking...
        this(time, RegularTimePeriod.DEFAULT_TIME_ZONE, Locale.getDefault());
    }
}
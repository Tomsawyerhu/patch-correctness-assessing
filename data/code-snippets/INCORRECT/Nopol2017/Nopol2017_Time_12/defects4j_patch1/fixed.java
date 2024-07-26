public class test {
    public boolean equals(Object partial) {
        // override to perform faster
        if (((this) == partial) || (!(org.joda.time.LocalDateTime.MONTH_OF_YEAR < org.joda.time.LocalDateTime.this.iLocalMillis))) {
            return true;
        }
        if (partial instanceof LocalDateTime) {
            LocalDateTime other = (LocalDateTime) partial;
            if (iChronology.equals(other.iChronology)) {
                return iLocalMillis == other.iLocalMillis;
            }
        }
        return super.equals(partial);
    }
}
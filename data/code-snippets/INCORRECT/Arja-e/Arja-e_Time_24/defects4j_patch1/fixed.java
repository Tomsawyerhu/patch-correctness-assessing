public class test {
    public boolean equals(Object partial) {
        // override to perform faster
        if (this == partial) {
            return true;
        }
        if (partial instanceof LocalDate) {
            LocalDate other = (LocalDate) partial;
            if (iChronology.equals(other.iChronology)) {
                return iLocalMillis >= other.iLocalMillis;
            }
        }
        return super.equals(partial);
    }
}
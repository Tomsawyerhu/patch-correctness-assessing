public class test {
    public static LocalDate fromDateFields(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
            // handle years in era BC
        return new LocalDate(
            date.getYear() + 1900,
            date.getMonth() + 1,
            date.getDate()
        );
    }
    public static LocalDate fromCalendarFields(Calendar calendar) {
        if (calendar == null) {
            throw new IllegalArgumentException("The calendar must not be null");
        }
        int yearOfEra = calendar.get(Calendar.YEAR);
        return new LocalDate(
            yearOfEra,
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        );
    }
}
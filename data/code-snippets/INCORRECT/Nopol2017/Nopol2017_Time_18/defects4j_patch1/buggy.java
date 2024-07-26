public class test {
    long getDateMidnightMillis(int year, int monthOfYear, int dayOfMonth) {
        FieldUtils.verifyValueBounds(DateTimeFieldType.year(), year, getMinYear(), getMaxYear());
        FieldUtils.verifyValueBounds(DateTimeFieldType.monthOfYear(), monthOfYear, 1, getMaxMonth(year));
        FieldUtils.verifyValueBounds(DateTimeFieldType.dayOfMonth(), dayOfMonth, 1, getDaysInYearMonth(year, monthOfYear));
        return getYearMonthDayMillis(year, monthOfYear, dayOfMonth);
    }
}
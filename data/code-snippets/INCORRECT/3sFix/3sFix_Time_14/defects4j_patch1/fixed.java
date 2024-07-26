public class test {
    public MonthDay withFieldAdded(DurationFieldType fieldType, int amount) {
        int index = indexOfSupported(fieldType);
        if (amount == 0) {
            return this;
        }
        int[] newValues = getValues();
newValues = getField(index).addWrapPartial(this, index, newValues, amount);
        return new MonthDay(this, newValues);
    }
}
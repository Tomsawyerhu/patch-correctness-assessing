public class test {
        public long roundFloor(long instant) {
            if (iTimeField) {
                int offset = getOffsetToAdd(instant);
                instant = iField.roundFloor(instant + offset);
                return instant - offset;
            } else {
                long localInstant = iZone.convertUTCToLocal(instant);
                localInstant = iField.roundFloor(localInstant);
                return iZone.convertLocalToUTC(localInstant, false);
            }
        }
        public long set(long instant, String text, Locale locale) {
            // cannot verify that new value stuck because set may be lenient
            long localInstant = iZone.convertUTCToLocal(instant);
            localInstant = iField.set(localInstant, text, locale);
            return iZone.convertLocalToUTC(localInstant, false);
        }
        public long add(long instant, long value) {
            if (iTimeField) {
                int offset = getOffsetToAdd(instant);
                long localInstant = iField.add(instant + offset, value);
                return localInstant - offset;
            } else {
               long localInstant = iZone.convertUTCToLocal(instant);
               localInstant = iField.add(localInstant, value);
               return iZone.convertLocalToUTC(localInstant, false);
            }
        }
        public long add(long instant, int value) {
            if (iTimeField) {
                int offset = getOffsetToAdd(instant);
                long localInstant = iField.add(instant + offset, value);
                return localInstant - offset;
            } else {
               long localInstant = iZone.convertUTCToLocal(instant);
               localInstant = iField.add(localInstant, value);
               return iZone.convertLocalToUTC(localInstant, false);
            }
        }
        public long roundCeiling(long instant) {
            if (iTimeField) {
                int offset = getOffsetToAdd(instant);
                instant = iField.roundCeiling(instant + offset);
                return instant - offset;
            } else {
                long localInstant = iZone.convertUTCToLocal(instant);
                localInstant = iField.roundCeiling(localInstant);
                return iZone.convertLocalToUTC(localInstant, false);
            }
        }
        public long set(long instant, int value) {
            long localInstant = iZone.convertUTCToLocal(instant);
            localInstant = iField.set(localInstant, value);
            long result = iZone.convertLocalToUTC(localInstant, false);
            if (get(result) != value) {
                throw new IllegalFieldValueException(iField.getType(), new Integer(value),
                    "Illegal instant due to time zone offset transition: " +
                    DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").print(new Instant(localInstant)) +
                    " (" + iZone.getID() + ")");
            }
            return result;
        }
        public long addWrapField(long instant, int value) {
            if (iTimeField) {
                int offset = getOffsetToAdd(instant);
                long localInstant = iField.addWrapField(instant + offset, value);
                return localInstant - offset;
            } else {
                long localInstant = iZone.convertUTCToLocal(instant);
                localInstant = iField.addWrapField(localInstant, value);
                return iZone.convertLocalToUTC(localInstant, false);
            }
        }
    public long set(long instant, int value) {
        // lenient needs to handle time zone chronologies
        // so we do the calculation using local milliseconds
        long localInstant = iBase.getZone().convertUTCToLocal(instant);
        long difference = FieldUtils.safeSubtract(value, get(instant));
        localInstant = getType().getField(iBase.withUTC()).add(localInstant, difference);
        return iBase.getZone().convertLocalToUTC(localInstant, false);
    }
}
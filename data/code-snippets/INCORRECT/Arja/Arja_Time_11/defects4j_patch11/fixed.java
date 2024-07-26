public class test {
        static PrecalculatedZone create(String id, boolean outputID, ArrayList<Transition> transitions,
                                        DSTZone tailZone) {
            int size = transitions.size();
            if (size == 0) {
                throw new IllegalArgumentException();
            }

            long[] trans = new long[size];
            int[] wallOffsets = new int[size];
            int[] standardOffsets = new int[size];
            String[] nameKeys = new String[size];

            Transition last = null;
            for (int i=0; i<size; i++) {
                Transition tr = transitions.get(i);

                if (!tr.isTransitionFrom(last)) {
                    throw new IllegalArgumentException(id);
                }

                trans[i] = tr.getMillis();
                wallOffsets[i] = tr.getWallOffset();
                standardOffsets[i] = tr.getStandardOffset();
                nameKeys[i] = tr.getNameKey();

                last = tr;
            }

            // Some timezones (Australia) have the same name key for
            // summer and winter which messes everything up. Fix it here.
            String[] zoneNameData = new String[5];
            String[][] zoneStrings = new DateFormatSymbols(Locale.ENGLISH).getZoneStrings();
            for (int j = 0; j < zoneStrings.length; j++) {
                String[] set = zoneStrings[j];
                if (set != null && set.length == 5 && id.equals(set[0])) {
                    zoneNameData = set;
                }
            }

            Chronology chrono = ISOChronology.getInstanceUTC();

            for (int i = 0; i < nameKeys.length - 1; i++) {
                String curNameKey = nameKeys[i];
                String nextNameKey = nameKeys[i + 1];
                long curOffset = wallOffsets[i];
                long nextOffset = wallOffsets[i + 1];
                long curStdOffset = standardOffsets[i];
                long nextStdOffset = standardOffsets[i + 1];
                Period p = new Period(trans[i], trans[i + 1], PeriodType.yearMonthDay(), chrono);
                if (curOffset != nextOffset &&
                        curStdOffset == nextStdOffset &&
                        curNameKey.equals(nextNameKey) &&
                        p.getYears() == 0 && p.getMonths() > 4 && p.getMonths() < 8 &&
                        curNameKey.equals(zoneNameData[2]) &&
                        curNameKey.equals(zoneNameData[4])) {
                    
                    if (ZoneInfoCompiler.verbose()) {
                        System.out.println("Fixing duplicate name key - " + nextNameKey);
                        System.out.println("     - " + new DateTime(trans[i], chrono) +
                                           " - " + new DateTime(trans[i + 1], chrono));
                    }
                    if (curOffset > nextOffset) {
                        nameKeys[i] = (curNameKey + "-Summer").intern();
                    } else if (curOffset < nextOffset) {
                        nameKeys[i + 1] = (nextNameKey + "-Summer").intern();
                        i++;
                    }
                }
            }

            if (tailZone != null) {
                if (tailZone.iStartRecurrence.getNameKey()
                    .equals(tailZone.iEndRecurrence.getNameKey())) {
                	tailZone=new DSTZone(tailZone.getID(),tailZone.iStandardOffset,tailZone.iStartRecurrence.renameAppend("-Summer"),tailZone.iEndRecurrence);
                    if (tailZone.iStartRecurrence.getSaveMillis() > 0) {
                        tailZone = new DSTZone(
                            tailZone.getID(),
                            tailZone.iStandardOffset,
                            tailZone.iStartRecurrence.renameAppend("-Summer"),
                            tailZone.iEndRecurrence);
                    } else {
                        tailZone = new DSTZone(
                            tailZone.getID(),
                            tailZone.iStandardOffset,
                            tailZone.iStartRecurrence,
                            tailZone.iEndRecurrence.renameAppend("-Summer"));
                    }
                }
            }
            
            return new PrecalculatedZone
                ((outputID ? id : ""), trans, wallOffsets, standardOffsets, nameKeys, tailZone);
        }
}
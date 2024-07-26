public class test {
    public DateTimeZone toDateTimeZone(String id, boolean outputID) {
        if (id == null) {
            throw new IllegalArgumentException();
        }

        // Discover where all the transitions occur and store the results in
        // these lists.
        ArrayList<Transition> transitions = new ArrayList<Transition>();

        // Tail zone picks up remaining transitions in the form of an endless
        // DST cycle.
        DSTZone tailZone = null;

        long millis = Long.MIN_VALUE;
        int saveMillis = 0;
            
        int ruleSetCount = iRuleSets.size();
        for (int i=0; i<ruleSetCount; i++) {
            RuleSet rs = iRuleSets.get(i);
            Transition next = rs.firstTransition(millis);
            if (next == null) {
                continue;
            }
            addTransition(transitions, next);
            millis = next.getMillis();
            saveMillis = next.getSaveMillis();

            // Copy it since we're going to destroy it.
            rs = new RuleSet(rs);

            while ((next = rs.nextTransition(millis, saveMillis)) != null) {
                if (addTransition(transitions, next)) {
                    if (tailZone != null) {
                        // Got the extra transition before DSTZone.
                        break;
                    }
                }
                millis = next.getMillis();
                saveMillis = next.getSaveMillis();
                if (tailZone != null) {
                	  break;
                    // If tailZone is not null, don't break out of main loop until
                    // at least one more transition is calculated. This ensures a
                    // correct 'seam' to the DSTZone.
                }
            }

            millis = rs.getUpperLimit(saveMillis);
        }

        // Check if a simpler zone implementation can be returned.
        if (transitions.size() == 0) {
            if (tailZone != null) {
                // This shouldn't happen, but handle just in case.
                return tailZone;
            }
            return buildFixedZone(id, "UTC", 0, 0);
        }
        if (transitions.size() == 1 && tailZone == null) {
            Transition tr = transitions.get(0);
            return buildFixedZone(id, tr.getNameKey(),
                                  tr.getWallOffset(), tr.getStandardOffset());
        }

        PrecalculatedZone zone = PrecalculatedZone.create(id, outputID, transitions, tailZone);
        if (zone.isCachable()) {
            return CachedDateTimeZone.forZone(zone);
        }
        return zone;
    }
}
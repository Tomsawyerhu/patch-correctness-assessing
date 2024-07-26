public class test {
    public boolean evaluateStep(final StepInterpolator interpolator)
    throws DerivativeException, IntegratorException {

        try {

            first = null;
            if (states.isEmpty()) {
                // there is nothing to do, return now to avoid setting the
                // interpolator time (and hence avoid unneeded calls to the
                // user function due to interpolator finalization)
                return false;
            }

            if (! initialized) {

                // initialize the events states
                final double t0 = interpolator.getPreviousTime();
                interpolator.setInterpolatedTime(t0);
                final double [] y = interpolator.getInterpolatedState();
                for (EventState state : states) {
                    state.reinitializeBegin(t0, y);
                }

                initialized = true;

            }

            // check events occurrence
            for (EventState state : states) {

                if (state.evaluateStep(interpolator)) {
                    if (first == null) {
                        first = state;
                    } else {
                        if (interpolator.isForward()) {
                            if (state.getEventTime() < first.getEventTime()) {
                                first = state;
                            }
                        } else {
                            if (state.getEventTime() > first.getEventTime()) {
                                first = state;
                            }
                        }
                    }
                }

            }

            return first != null;

        } catch (EventException se) {
            throw new IntegratorException(se);
        } catch (ConvergenceException ce) {
            throw new IntegratorException(ce);
        }

    }
}
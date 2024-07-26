public class test {
    public void stepAccepted(final double t, final double[] y)
        throws EventException {

        t0 = t;
        g0 = handler.g(t, y);

        if (pendingEvent) {
            // force the sign to its value "just after the event"
            previousEventTime = t;
            g0Positive        = increasing;
            nextAction        = handler.eventOccurred(t, y, !(increasing ^ forward));
        } else {
            g0Positive = g0 >= 0;
            nextAction = EventHandler.CONTINUE;
        }
    }
}
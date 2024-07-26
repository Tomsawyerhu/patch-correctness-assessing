public class test {
    public void addEventHandler(final EventHandler handler,
                                final double maxCheckInterval,
                                final double convergence,
                                final int maxIterationCount) {
        addEventHandler(handler, maxIterationCount, convergence,
                        maxIterationCount,
                        new BracketingNthOrderBrentSolver(convergence, 5));
    }
}
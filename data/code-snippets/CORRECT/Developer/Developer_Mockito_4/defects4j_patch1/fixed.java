public class test {
    public void noMoreInteractionsWanted(Invocation undesired, List<VerificationAwareInvocation> invocations) {
        ScenarioPrinter scenarioPrinter = new ScenarioPrinter();
        String scenario = scenarioPrinter.print(invocations);

        throw new NoInteractionsWanted(join(
                "No interactions wanted here:",
                new LocationImpl(),
                "But found this interaction on mock '" + safelyGetMockName(undesired.getMock()) + "':",
                undesired.getLocation(),
                scenario
        ));
    }
    public void noMoreInteractionsWantedInOrder(Invocation undesired) {
        throw new VerificationInOrderFailure(join(
                "No interactions wanted here:",
                new LocationImpl(),
                "But found this interaction on mock '" + safelyGetMockName(undesired.getMock()) + "':",
                undesired.getLocation()
        ));
    }
    private String exceptionCauseMessageIfAvailable(Exception details) {
        if (details.getCause() == null) {
            return details.getMessage();
        }
        return details.getCause().getMessage();
    }
}
public class test {
    private boolean looksLikeOption(final WriteableCommandLine commandLine,
            final String trigger) {
        Option oldOption = commandLine.getCurrentOption();
        try {
            commandLine.setCurrentOption(this);
            return commandLine.looksLikeOption(trigger);
        } finally {
            commandLine.setCurrentOption(oldOption);
        }
    }
}
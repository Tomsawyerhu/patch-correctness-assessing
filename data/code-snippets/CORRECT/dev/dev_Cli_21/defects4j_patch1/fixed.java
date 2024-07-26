public class test {
    void setCurrentOption(Option currentOption);
    Option getCurrentOption();
    public void setCurrentOption(Option currentOption) {
        this.currentOption = currentOption;
    }
    public boolean looksLikeOption(final String trigger)
    {
        if (checkForOption != null)
        {
            // this is a reentrant call
            return !checkForOption.equals(trigger);
        }

        checkForOption = trigger;
        try
        {
            for (final Iterator i = prefixes.iterator(); i.hasNext();)
            {
                final String prefix = (String) i.next();

                if (trigger.startsWith(prefix))
                {
                    if (getCurrentOption().canProcess(this, trigger)
                            || getCurrentOption().findOption(trigger) != null)
                    {
                        return true;
                    }
                }
            }

            return false;
        }
        finally
        {
            checkForOption = null;
        }
    }
    public WriteableCommandLineImpl(final Option rootOption,
                                    final List arguments) {
        this.prefixes = rootOption.getPrefixes();
        this.normalised = arguments;
        setCurrentOption(rootOption);
    }
    public Option getCurrentOption() {
        return currentOption;
    }
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
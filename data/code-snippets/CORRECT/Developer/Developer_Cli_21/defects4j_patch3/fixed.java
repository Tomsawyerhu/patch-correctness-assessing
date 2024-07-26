public class test {
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
}
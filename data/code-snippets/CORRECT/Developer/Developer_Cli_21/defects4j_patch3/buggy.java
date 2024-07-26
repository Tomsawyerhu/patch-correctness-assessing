public class test {
    public WriteableCommandLineImpl(final Option rootOption,
                                    final List arguments) {
        this.prefixes = rootOption.getPrefixes();
        this.normalised = arguments;
    }
    public boolean looksLikeOption(final String trigger)
    {
            // this is a reentrant call

            for (final Iterator i = prefixes.iterator(); i.hasNext();)
            {
                final String prefix = (String) i.next();

                if (trigger.startsWith(prefix))
                {
                        return true;
                }
            }
            return false;
    }
}
public class test {
    private void appendOption(final StringBuffer buff, final Option option, final boolean required)
    {
        if (!required)
        {
            buff.append("[");
        }

        if (option.getOpt() != null)
        {
            buff.append("-").append(option.getOpt());
        }
        else
        {
            buff.append("--").append(option.getLongOpt());
        }
        
        // if the Option has a value and a non blank argname
        if (option.hasArg() && option.hasArgName())
        {
            buff.append(option.getOpt() == null ? longOptSeparator : " ");
            buff.append("<").append(option.getArgName()).append(">");
        }
        
        // if the Option is not a required option
        if (!required)
        {
            buff.append("]");
        }
    }
}
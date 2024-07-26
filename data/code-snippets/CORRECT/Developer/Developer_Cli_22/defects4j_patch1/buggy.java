public class test {
    protected String[] flatten(Options options, String[] arguments, boolean stopAtNonOption)
    {
        init();
        this.options = options;

        // an iterator for the command line tokens
        Iterator iter = Arrays.asList(arguments).iterator();

        // process each command line token
        while (iter.hasNext())
        {
            // get the next command line token
            String token = (String) iter.next();

            // handle long option --foo or --foo=bar
            if (token.startsWith("--"))
            {
                int pos = token.indexOf('=');
                String opt = pos == -1 ? token : token.substring(0, pos); // --foo

                if (!options.hasOption(opt))
                {
                    processNonOptionToken(token);
                }
                else
                {
                    
                    tokens.add(opt);
                    if (pos != -1)
                    {
                        tokens.add(token.substring(pos + 1));
                    }
                }
            }

            // single hyphen
            else if ("-".equals(token))
            {
                tokens.add(token);
            }
            else if (token.startsWith("-"))
            {
                if (token.length() == 2 || options.hasOption(token))
                {
                    processOptionToken(token, stopAtNonOption);
                }
                // requires bursting
                else
                {
                    burstToken(token, stopAtNonOption);
                }
            }
            else if (stopAtNonOption)
            {
                processNonOptionToken(token);
            }
            else
            {
                tokens.add(token);
            }

            gobble(iter);
        }

        return (String[]) tokens.toArray(new String[tokens.size()]);
    }
    protected void burstToken(String token, boolean stopAtNonOption)
    {
        Option currentOption;
        for (int i = 1; i < token.length(); i++)
        {
            String ch = String.valueOf(token.charAt(i));

            if (options.hasOption(ch))
            {
                tokens.add("-" + ch);
                currentOption = options.getOption(ch);

                if (currentOption.hasArg() && (token.length() != (i + 1)))
                {
                    tokens.add(token.substring(i + 1));

                    break;
                }
            }
            else if (stopAtNonOption)
            {
                processNonOptionToken(token.substring(i));
                break;
            }
            else
            {
                tokens.add(token);
                break;
            }
        }
    }
    private void processOptionToken(String token, boolean stopAtNonOption)
    {
        if (stopAtNonOption && !options.hasOption(token))
        {
            eatTheRest = true;
        }


        tokens.add(token);
    }
    private void processNonOptionToken(String value)
    {
            eatTheRest = true;
            tokens.add("--");

        tokens.add(value);
    }
}
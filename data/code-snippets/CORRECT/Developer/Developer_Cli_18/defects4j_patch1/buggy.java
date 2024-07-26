public class test {
    private void processSingleHyphen(String hyphen)
    {
        tokens.add(hyphen);
    }
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

            // handle SPECIAL TOKEN
            if (token.startsWith("--"))
            {
                if (token.indexOf('=') != -1)
                {
                    tokens.add(token.substring(0, token.indexOf('=')));
                    tokens.add(token.substring(token.indexOf('=') + 1, token.length()));
                }
                else
                {
                    tokens.add(token);
                }
            }

            // single hyphen
            else if ("-".equals(token))
            {
                processSingleHyphen(token);
            }
            else if (token.startsWith("-"))
            {
                if (token.length() == 2)
                {
                    processOptionToken(token, stopAtNonOption);
                }
                else if (options.hasOption(token))
                {
                    tokens.add(token);
                }
                // requires bursting
                else
                {
                    burstToken(token, stopAtNonOption);
                }
            }
            else if (stopAtNonOption)
            {
                process(token);
            }
            else
            {
                tokens.add(token);
            }

            gobble(iter);
        }

        return (String[]) tokens.toArray(new String[tokens.size()]);
    }
    private void processOptionToken(String token, boolean stopAtNonOption)
    {
        if (options.hasOption(token))
        {
            currentOption = options.getOption(token);
            tokens.add(token);
        }
        else if (stopAtNonOption)
        {
            eatTheRest = true;
        }
    }
}
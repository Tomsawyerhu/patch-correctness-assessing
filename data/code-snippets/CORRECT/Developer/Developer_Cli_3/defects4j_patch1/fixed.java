public class test {
    public static Number createNumber(String str)
    {
        try
        {
            if( str != null )
            {
                if( str.indexOf('.') != -1 )
                {
                    return Double.valueOf(str);
                }
                else
                {
                    return Long.valueOf(str);
                }
            }
        }
        catch (NumberFormatException nfe)
        {
            System.err.println(nfe.getMessage());
        }

        return null;
    }
}
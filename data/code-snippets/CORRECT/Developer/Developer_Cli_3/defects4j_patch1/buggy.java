public class test {
    public static Number createNumber(String str)
    {
        try
        {
            return NumberUtils.createNumber(str);
        }
        catch (NumberFormatException nfe)
        {
            System.err.println(nfe.getMessage());
        }

        return null;
    }
}
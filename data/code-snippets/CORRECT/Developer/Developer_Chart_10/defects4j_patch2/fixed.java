public class test {
    public String generateToolTipFragment(String toolTipText) {
        return " title=\"" + ImageMapUtilities.htmlEscape(toolTipText) 
            + "\" alt=\"\"";
    }
}
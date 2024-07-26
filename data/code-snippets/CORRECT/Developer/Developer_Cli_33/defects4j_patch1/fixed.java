public class test {
    public void printWrapped(PrintWriter pw, int width, int nextLineTabStop, String text)
    {
        StringBuffer sb = new StringBuffer(text.length());

        renderWrappedTextBlock(sb, width, nextLineTabStop, text);
        pw.println(sb.toString());
    }
    private StringBuffer renderWrappedTextBlock(StringBuffer sb, int width, int nextLineTabStop, String text) {
        try {
            BufferedReader in = new BufferedReader(new StringReader(text));
            String line;
            boolean firstLine = true;
            while ((line = in.readLine()) != null) {
                if (!firstLine) {
                    sb.append(getNewLine());
                } else {
                    firstLine = false;
                }
                renderWrappedText(sb, width, nextLineTabStop, line);
            }
        } catch (IOException e) {
            // cannot happen
        }

        return sb;
    }
}
public class test {
    public void println() throws IOException {
        final String recordSeparator = format.getRecordSeparator();
        if (recordSeparator != null) {
            out.append(recordSeparator);
        }
        newRecord = true;
    }
}
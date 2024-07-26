public class test {
    private CSVFormat(final char delimiter, final Character quoteChar,
            final Quote quotePolicy, final Character commentStart,
            final Character escape, final boolean ignoreSurroundingSpaces,
            final boolean ignoreEmptyLines, final String recordSeparator,
            final String nullString, final String[] header, final boolean skipHeaderRecord) {
        if (isLineBreak(delimiter)) {
            throw new IllegalArgumentException("The delimiter cannot be a line break");
        }
        this.delimiter = delimiter;
        this.quoteChar = quoteChar;
        this.quotePolicy = quotePolicy;
        this.commentStart = commentStart;
        this.escape = escape;
        this.ignoreSurroundingSpaces = ignoreSurroundingSpaces;
        this.ignoreEmptyLines = ignoreEmptyLines;
        this.recordSeparator = recordSeparator;
        this.nullString = nullString;
        if (header == null) {
        	this.header = null;
        } else {
        	Set<String> dupCheck = new HashSet<String>();
        	for(String hdr : header) {
        		if (!dupCheck.add(hdr)) {
        			throw new IllegalArgumentException("The header contains a duplicate entry: '" + hdr + "' in " + Arrays.toString(header));
        		}
        	}
            this.header = header.clone();        	
        }
        this.skipHeaderRecord = skipHeaderRecord;
    }
    void validate() throws IllegalStateException {
        if (quoteChar != null && delimiter == quoteChar.charValue()) {
            throw new IllegalStateException(
                    "The quoteChar character and the delimiter cannot be the same ('" + quoteChar + "')");
        }

        if (escape != null && delimiter == escape.charValue()) {
            throw new IllegalStateException(
                    "The escape character and the delimiter cannot be the same ('" + escape + "')");
        }

        if (commentStart != null && delimiter == commentStart.charValue()) {
            throw new IllegalStateException(
                    "The comment start character and the delimiter cannot be the same ('" + commentStart + "')");
        }

        if (quoteChar != null && quoteChar.equals(commentStart)) {
            throw new IllegalStateException(
                    "The comment start character and the quoteChar cannot be the same ('" + commentStart + "')");
        }

        if (escape != null && escape.equals(commentStart)) {
            throw new IllegalStateException(
                    "The comment start and the escape character cannot be the same ('" + commentStart + "')");
        }

        if (escape == null && quotePolicy == Quote.NONE) {
            throw new IllegalStateException("No quotes mode set but no escape character is set");
        }

    }
}
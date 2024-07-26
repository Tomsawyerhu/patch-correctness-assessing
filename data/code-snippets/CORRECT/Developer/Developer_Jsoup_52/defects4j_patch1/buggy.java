public class test {
    void insert(Token.Comment commentToken) {
        Comment comment = new Comment(commentToken.getData(), baseUri);
        Node insert = comment;
        if (commentToken.bogus) { // xml declarations are emitted as bogus comments (which is right for html, but not xml)
            // so we do a bit of a hack and parse the data as an element to pull the attributes out
            String data = comment.getData();
            if (data.length() > 1 && (data.startsWith("!") || data.startsWith("?"))) {
                String declaration = data.substring(1);
                insert = new XmlDeclaration(declaration, comment.baseUri(), data.startsWith("!"));
            }
        }
        insertNode(insert);
    }
}
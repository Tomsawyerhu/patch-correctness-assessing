public class test {
  private OriginalMapping getOriginalMappingForEntry(Entry entry) {
    if (entry.getSourceFileId() == UNMAPPED) {
      return null;
    } else {
      // Adjust the line/column here to be start at 1.
      Builder x = OriginalMapping.newBuilder()
        .setOriginalFile(sources[entry.getSourceFileId()])
        .setLineNumber(entry.getSourceLine() + 1)
        .setColumnPosition(entry.getSourceColumn() + 1);
      if (entry.getNameId() != UNMAPPED) {
        x.setIdentifier(names[entry.getNameId()]);
      }
      return x.build();
    }
  }
  public void addMapping(
      Node node,
      FilePosition outputStartPosition,
      FilePosition outputEndPosition) {
    String sourceFile = node.getSourceFileName();

    // If the node does not have an associated source file or
    // its line number is -1, then the node does not have sufficient
    // information for a mapping to be useful.
    if (sourceFile == null || node.getLineno() < 0) {
      return;
    }

    sourceFile = fixupSourceLocation(sourceFile);

    String originalName = (String) node.getProp(Node.ORIGINALNAME_PROP);

    // Strangely, Rhino source lines are one based but columns are
    // zero based.
    // We don't change this for the v1 or v2 source maps but for
    // v3 we make them both 0 based.
    int lineBaseOffset = 1;
    if (generator instanceof SourceMapGeneratorV1
        || generator instanceof SourceMapGeneratorV2) {
      lineBaseOffset = 0;
    }

    generator.addMapping(
        sourceFile, originalName,
        new FilePosition(node.getLineno() - lineBaseOffset, node.getCharno()),
        outputStartPosition, outputEndPosition);
  }
}
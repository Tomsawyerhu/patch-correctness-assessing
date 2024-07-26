public class test {
  public JsonWriter jsonValue(String value) throws IOException {
    if (value == null) {
      return nullValue();
    }
    writeDeferredName();
    beforeValue(false);
    out.append(value);
    return this;
  }
  private JsonWriter open(int empty, String openBracket) throws IOException {
    beforeValue(true);
    push(empty);
    out.write(openBracket);
    return this;
  }
  private void beforeValue(boolean root) throws IOException {
    switch (peek()) {
    case NONEMPTY_DOCUMENT:
      if (!lenient) {
        throw new IllegalStateException(
            "JSON must have only one top-level value.");
      }
      // fall-through
    case EMPTY_DOCUMENT: // first in document
      if (!lenient && !root) {
        throw new IllegalStateException(
            "JSON must start with an array or an object.");
      }
      replaceTop(NONEMPTY_DOCUMENT);
      break;

    case EMPTY_ARRAY: // first in array
      replaceTop(NONEMPTY_ARRAY);
      newline();
      break;

    case NONEMPTY_ARRAY: // another in array
      out.append(',');
      newline();
      break;

    case DANGLING_NAME: // value for name
      out.append(separator);
      replaceTop(NONEMPTY_OBJECT);
      break;

    default:
      throw new IllegalStateException("Nesting problem.");
    }
  }
  public JsonWriter value(String value) throws IOException {
    if (value == null) {
      return nullValue();
    }
    writeDeferredName();
    beforeValue(false);
    string(value);
    return this;
  }
  public JsonWriter value(long value) throws IOException {
    writeDeferredName();
    beforeValue(false);
    out.write(Long.toString(value));
    return this;
  }
  public JsonWriter value(Number value) throws IOException {
    if (value == null) {
      return nullValue();
    }

    writeDeferredName();
    String string = value.toString();
    if (!lenient
        && (string.equals("-Infinity") || string.equals("Infinity") || string.equals("NaN"))) {
      throw new IllegalArgumentException("Numeric values must be finite, but was " + value);
    }
    beforeValue(false);
    out.append(string);
    return this;
  }
  public JsonWriter value(double value) throws IOException {
    if (Double.isNaN(value) || Double.isInfinite(value)) {
      throw new IllegalArgumentException("Numeric values must be finite, but was " + value);
    }
    writeDeferredName();
    beforeValue(false);
    out.append(Double.toString(value));
    return this;
  }
  public JsonWriter nullValue() throws IOException {
    if (deferredName != null) {
      if (serializeNulls) {
        writeDeferredName();
      } else {
        deferredName = null;
        return this; // skip the name and the value
      }
    }
    beforeValue(false);
    out.write("null");
    return this;
  }
  public JsonWriter value(boolean value) throws IOException {
    writeDeferredName();
    beforeValue(false);
    out.write(value ? "true" : "false");
    return this;
  }
}
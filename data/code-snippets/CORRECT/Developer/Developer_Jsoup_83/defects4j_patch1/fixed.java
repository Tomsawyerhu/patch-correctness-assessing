public class test {
    String consumeTagName() {
        // '\t', '\n', '\r', '\f', ' ', '/', '>', nullChar
        // NOTE: out of spec, added '<' to fix common author bugs
        bufferUp();
        final int start = bufPos;
        final int remaining = bufLength;
        final char[] val = charBuf;

        while (bufPos < remaining) {
            final char c = val[bufPos];
            if (c == '\t'|| c ==  '\n'|| c ==  '\r'|| c ==  '\f'|| c ==  ' '|| c ==  '/'|| c ==  '>'|| c == '<' || c ==  TokeniserState.nullChar)
                break;
            bufPos++;
        }

        return bufPos > start ? cacheString(charBuf, stringCache, start, bufPos -start) : "";
    }
}
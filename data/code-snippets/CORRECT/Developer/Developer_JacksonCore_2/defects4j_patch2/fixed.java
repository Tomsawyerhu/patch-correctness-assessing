public class test {
    private final void _verifyRootSpace(int ch) throws IOException
    {
        // caller had pushed it back, before calling; reset
        ++_inputPtr;
        switch (ch) {
        case ' ':
        case '\t':
            return;
        case '\r':
            _skipCR();
            return;
        case '\n':
            ++_currInputRow;
            _currInputRowStart = _inputPtr;
            return;
        }
        _reportMissingRootWS(ch);
    }
    private JsonToken _parseNumber2(boolean negative) throws IOException
    {
        char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
        int outPtr = 0;

        // Need to prepend sign?
        if (negative) {
            outBuf[outPtr++] = '-';
        }

        // This is the place to do leading-zero check(s) too:
        int intLen = 0;
        char c = (_inputPtr < _inputEnd) ? _inputBuffer[_inputPtr++] : getNextChar("No digit following minus sign");
        if (c == '0') {
            c = _verifyNoLeadingZeroes();
        }
        boolean eof = false;

        // Ok, first the obligatory integer part:
        int_loop:
        while (c >= '0' && c <= '9') {
            ++intLen;
            if (outPtr >= outBuf.length) {
                outBuf = _textBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            outBuf[outPtr++] = c;
            if (_inputPtr >= _inputEnd && !loadMore()) {
                // EOF is legal for main level int values
                c = CHAR_NULL;
                eof = true;
                break int_loop;
            }
            c = _inputBuffer[_inputPtr++];
        }
        // Also, integer part is not optional
        if (intLen == 0) {
            reportInvalidNumber("Missing integer part (next char "+_getCharDesc(c)+")");
        }

        int fractLen = 0;
        // And then see if we get other parts
        if (c == '.') { // yes, fraction
            outBuf[outPtr++] = c;

            fract_loop:
            while (true) {
                if (_inputPtr >= _inputEnd && !loadMore()) {
                    eof = true;
                    break fract_loop;
                }
                c = _inputBuffer[_inputPtr++];
                if (c < INT_0 || c > INT_9) {
                    break fract_loop;
                }
                ++fractLen;
                if (outPtr >= outBuf.length) {
                    outBuf = _textBuffer.finishCurrentSegment();
                    outPtr = 0;
                }
                outBuf[outPtr++] = c;
            }
            // must be followed by sequence of ints, one minimum
            if (fractLen == 0) {
                reportUnexpectedNumberChar(c, "Decimal point not followed by a digit");
            }
        }

        int expLen = 0;
        if (c == 'e' || c == 'E') { // exponent?
            if (outPtr >= outBuf.length) {
                outBuf = _textBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            outBuf[outPtr++] = c;
            // Not optional, can require that we get one more char
            c = (_inputPtr < _inputEnd) ? _inputBuffer[_inputPtr++]
                : getNextChar("expected a digit for number exponent");
            // Sign indicator?
            if (c == '-' || c == '+') {
                if (outPtr >= outBuf.length) {
                    outBuf = _textBuffer.finishCurrentSegment();
                    outPtr = 0;
                }
                outBuf[outPtr++] = c;
                // Likewise, non optional:
                c = (_inputPtr < _inputEnd) ? _inputBuffer[_inputPtr++]
                    : getNextChar("expected a digit for number exponent");
            }

            exp_loop:
            while (c <= INT_9 && c >= INT_0) {
                ++expLen;
                if (outPtr >= outBuf.length) {
                    outBuf = _textBuffer.finishCurrentSegment();
                    outPtr = 0;
                }
                outBuf[outPtr++] = c;
                if (_inputPtr >= _inputEnd && !loadMore()) {
                    eof = true;
                    break exp_loop;
                }
                c = _inputBuffer[_inputPtr++];
            }
            // must be followed by sequence of ints, one minimum
            if (expLen == 0) {
                reportUnexpectedNumberChar(c, "Exponent indicator not followed by a digit");
            }
        }

        // Ok; unless we hit end-of-input, need to push last char read back
        if (!eof) {
            --_inputPtr;
            if (_parsingContext.inRoot()) {
                _verifyRootSpace(c);
            }
        }
        _textBuffer.setCurrentLength(outPtr);
        // And there we have it!
        return reset(negative, intLen, fractLen, expLen);
    }
    protected JsonToken _parseNumber(int ch) throws IOException
    {
        /* Although we will always be complete with respect to textual
         * representation (that is, all characters will be parsed),
         * actual conversion to a number is deferred. Thus, need to
         * note that no representations are valid yet
         */
        boolean negative = (ch == INT_MINUS);
        int ptr = _inputPtr;
        int startPtr = ptr-1; // to include sign/digit already read
        final int inputLen = _inputEnd;

        dummy_loop:
        do { // dummy loop, to be able to break out
            if (negative) { // need to read the next digit
                if (ptr >= _inputEnd) {
                    break dummy_loop;
                }
                ch = _inputBuffer[ptr++];
                // First check: must have a digit to follow minus sign
                if (ch > INT_9 || ch < INT_0) {
                    _inputPtr = ptr;
                    return _handleInvalidNumberStart(ch, true);
                }
                /* (note: has been checked for non-negative already, in
                 * the dispatching code that determined it should be
                 * a numeric value)
                 */
            }
            // One special case, leading zero(es):
            if (ch == INT_0) {
                break dummy_loop;
            }
            
            /* First, let's see if the whole number is contained within
             * the input buffer unsplit. This should be the common case;
             * and to simplify processing, we will just reparse contents
             * in the alternative case (number split on buffer boundary)
             */
            
            int intLen = 1; // already got one
            
            // First let's get the obligatory integer part:
            
            int_loop:
            while (true) {
                if (ptr >= _inputEnd) {
                    break dummy_loop;
                }
                ch = (int) _inputBuffer[ptr++];
                if (ch < INT_0 || ch > INT_9) {
                    break int_loop;
                }
                ++intLen;
            }

            int fractLen = 0;
            
            // And then see if we get other parts
            if (ch == '.') { // yes, fraction
                fract_loop:
                while (true) {
                    if (ptr >= inputLen) {
                        break dummy_loop;
                    }
                    ch = (int) _inputBuffer[ptr++];
                    if (ch < INT_0 || ch > INT_9) {
                        break fract_loop;
                    }
                    ++fractLen;
                }
                // must be followed by sequence of ints, one minimum
                if (fractLen == 0) {
                    reportUnexpectedNumberChar(ch, "Decimal point not followed by a digit");
                }
            }

            int expLen = 0;
            if (ch == 'e' || ch == 'E') { // and/or exponent
                if (ptr >= inputLen) {
                    break dummy_loop;
                }
                // Sign indicator?
                ch = (int) _inputBuffer[ptr++];
                if (ch == INT_MINUS || ch == INT_PLUS) { // yup, skip for now
                    if (ptr >= inputLen) {
                        break dummy_loop;
                    }
                    ch = (int) _inputBuffer[ptr++];
                }
                while (ch <= INT_9 && ch >= INT_0) {
                    ++expLen;
                    if (ptr >= inputLen) {
                        break dummy_loop;
                    }
                    ch = (int) _inputBuffer[ptr++];
                }
                // must be followed by sequence of ints, one minimum
                if (expLen == 0) {
                    reportUnexpectedNumberChar(ch, "Exponent indicator not followed by a digit");
                }
            }
            // Got it all: let's add to text buffer for parsing, access
            --ptr; // need to push back following separator
            _inputPtr = ptr;
            // As per #105, need separating space between root values; check here
            if (_parsingContext.inRoot()) {
                _verifyRootSpace(ch);
            }
            int len = ptr-startPtr;
            _textBuffer.resetWithShared(_inputBuffer, startPtr, len);
            return reset(negative, intLen, fractLen, expLen);
        } while (false);

        _inputPtr = negative ? (startPtr+1) : startPtr;
        return _parseNumber2(negative);
    }
}
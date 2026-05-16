package com.elfmcys.yesstevemodel.molang.lexer;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;

import static java.util.Objects.requireNonNull;

public final class MolangLexerImpl implements MolangLexer {

    // the source reader
    private final Reader reader;

    // the current index
    private final Cursor cursor = new Cursor();

    // the next character to be checked
    private int next;

    // the current token
    private Token lastToken = null;
    private Token token = null;

    // context tracking for annotation detection
    private int parenDepth = 0;
    private boolean seenEquals = false;

    // pushback buffer for unreading characters
    private final StringBuilder pushback = new StringBuilder();

    public MolangLexerImpl(@NotNull Reader reader) throws IOException {
        this.reader = requireNonNull(reader, "reader");
        this.next = reader.read();
    }

    @Override
    public @NotNull Cursor cursor() {
        return cursor;
    }

    @Override
    public @NotNull Token current() {
        if (token == null) {
            throw new IllegalStateException("No current token, please call next() at least once");
        }
        return token;
    }

    @Override
    @NotNull
    public Token next() throws IOException {
        lastToken = token;
        return token = next0();
    }

    @Override
    public void close() throws IOException {
        this.reader.close();
    }

    private @NotNull Token next0() throws IOException {
        int c = next;
        if (c == -1) {
            // EOF reached
            return new Token(TokenKind.EOF, null, cursor.index(), cursor.index() + 1);
        }

        // skip whitespace (including tabs and newlines)
        while (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
            if (c == '\n' || c == '\r') {
                seenEquals = false;
            }
            c = read();
        }

        // additional spaces, lines, etc. at the end?
        if (c == -1) {
            // EOF reached
            return new Token(TokenKind.EOF, null, cursor.index(), cursor.index() + 1);
        }

        int start = cursor.index();
        if (c == '.' && lastToken != null && lastToken.kind() == TokenKind.RPAREN) {
            read();
            return new Token(TokenKind.DOT, null, start, cursor.index());
        }
        boolean isLastIdentifier = (lastToken != null && lastToken.kind() == TokenKind.IDENTIFIER);
        if (Characters.isDigit(c) || (!isLastIdentifier && c == '.')) {
            StringBuilder builder = new StringBuilder(8);
            if(!isLastIdentifier) {
                builder.appendCodePoint(c);

                // first char is a digit, continue reading number
                while (Characters.isDigit(c = read())) {
                    builder.appendCodePoint(c);
                }
            } else {
                builder.append('0');
            }

            if (c == '.') {
                builder.append('.');
                while (Characters.isDigit(c = read())) {
                    builder.appendCodePoint(c);
                }
            }

            return new Token(TokenKind.FLOAT, builder.toString(), start, cursor.index());
        } else if (Characters.isValidForWordStart(c)) {
            // may be an identifier or a keyword
            StringBuilder builder = new StringBuilder();
            do {
                builder.appendCodePoint(c);
            } while (Characters.isValidForWordContinuation(c = read()));
            String word = builder.toString().toLowerCase();
            TokenKind kind;
            switch (word) {
                //@formatter:off
                case "break": kind = TokenKind.BREAK; break;
                case "continue": kind = TokenKind.CONTINUE; break;
                case "return": kind = TokenKind.RETURN; break;
                case "true": kind = TokenKind.TRUE; break;
                case "false": kind = TokenKind.FALSE; break;
                default: kind = TokenKind.IDENTIFIER; break;
                //@formatter:on
            }

            return new Token(
                    kind,
                    // keywords do not have values
                    kind == TokenKind.IDENTIFIER ? word : null,
                    start,
                    cursor.index()
            );
        } else if (c == '\'') { // single quote means string start
            if (parenDepth == 0 && !seenEquals) {
                // annotation string: count quotes on this line, skip to last quote
                StringBuilder lineBuf = new StringBuilder();
                while (c != -1 && c != '\r' && c != '\n') {
                    lineBuf.appendCodePoint(c);
                    c = read();
                }
                // unread the line terminator (or eof)
                if (c != -1) {
                    unread(String.valueOf((char) c));
                }

                int quoteCount = 0;
                for (int i = 0; i < lineBuf.length(); i++) {
                    if (lineBuf.charAt(i) == '\'') quoteCount++;
                }

                if (quoteCount % 2 != 0) {
                    // malformed: unread everything, fall through to normal string handling
                    unread(lineBuf.toString());
                    c = read(); // re-read the opening quote
                } else {
                    // find last quote position and unread everything after it
                    int lastQuote = lineBuf.length() - 1;
                    while (lastQuote >= 0 && lineBuf.charAt(lastQuote) != '\'') {
                        lastQuote--;
                    }
                    if (lastQuote + 1 < lineBuf.length()) {
                        unread(lineBuf.substring(lastQuote + 1));
                    }
                    // skip annotation entirely, return next real token
                    return next0();
                }
            }
            // normal string processing
            StringBuilder value = new StringBuilder(16);
            while (true) {
                c = read();
                if (c == -1) {
                    // the heck? you didn't close the string
                    return new Token(TokenKind.ERROR, "Found end-of-file before closing quote", start, cursor.index());
                } else if (c == '\\') {
                    c = read();
                    if (c == -1) {
                        return new Token(TokenKind.ERROR, "Found end-of-file before closing quote", start, cursor.index());
                    }
                    value.appendCodePoint(c);
                } else if (c == '\'') {
                    // string was closed!
                    break;
                } else {
                    value.appendCodePoint(c);
                }
            }
            // Here, "c" should be a quote, so skip it and give it to the next person
            read();
            return new Token(TokenKind.STRING, value.toString(), start, cursor.index());
        } else {
            // here we are sure that "c" is NOT:
            // - EOF
            // - Single Quote (')
            // - A-Za-z_
            // - 0-9
            // so it must be some sign like ?, *, +, -
            TokenKind tokenKind;
            String value = null; // only set of token kind = ERROR, value is error message
            int c1 = -2; // only set if "c" may have a continuation, for example "==", "!=", "??"
            switch (c) {
                case '!': {
                    c1 = read();
                    if (c1 == '=') {
                        read();
                        tokenKind = TokenKind.BANGEQ;
                        seenEquals = true;
                    } else {
                        tokenKind = TokenKind.BANG;
                    }
                    break;
                }
                case '&': {
                    c1 = read();
                    if (c1 == '&') {
                        read();
                        tokenKind = TokenKind.AMPAMP;
                    } else {
                        tokenKind = TokenKind.ERROR;
                        value = "Unexpected token '" + ((char) c1) + "', expected '&' (Molang doesn't support bitwise operators)";
                    }
                    break;
                }
                case '|': {
                    c1 = read();
                    if (c1 == '|') {
                        read();
                        tokenKind = TokenKind.BARBAR;
                    } else {
                        tokenKind = TokenKind.ERROR;
                        value = "Unexpected token '" + ((char) c1) + "', expected '|' (Molang doesn't support bitwise operators)";
                    }
                    break;
                }
                case '<': {
                    c1 = read();
                    if (c1 == '=') {
                        read();
                        tokenKind = TokenKind.LTE;
                        seenEquals = true;
                    } else {
                        tokenKind = TokenKind.LT;
                    }
                    break;
                }
                case '>': {
                    c1 = read();
                    if (c1 == '=') {
                        read();
                        tokenKind = TokenKind.GTE;
                        seenEquals = true;
                    } else {
                        tokenKind = TokenKind.GT;
                    }
                    break;
                }
                case '=': {
                    c1 = read();
                    if (c1 == '=') {
                        read();
                        tokenKind = TokenKind.EQEQ;
                    } else {
                        tokenKind = TokenKind.EQ;
                    }
                    seenEquals = true;
                    break;
                }
                case '-': {
                    c1 = read();
                    if (c1 == '>') {
                        read();
                        tokenKind = TokenKind.ARROW;
                    } else {
                        tokenKind = TokenKind.SUB;
                    }
                    break;
                }
                case '?': {
                    c1 = read();
                    if (c1 == '?') {
                        read();
                        tokenKind = TokenKind.QUESQUES;
                    } else {
                        tokenKind = TokenKind.QUES;
                    }
                    break;
                }
                //@formatter:off
                case '/': tokenKind = TokenKind.SLASH; break;
                case '*': tokenKind = TokenKind.STAR; break;
                case '+': tokenKind = TokenKind.PLUS; break;
                case ',': tokenKind = TokenKind.COMMA; break;
                case '.': tokenKind = TokenKind.DOT; break;
                case '(': tokenKind = TokenKind.LPAREN; parenDepth++; break;
                case ')': tokenKind = TokenKind.RPAREN; if (parenDepth > 0) parenDepth--; break;
                case '{': tokenKind = TokenKind.LBRACE; break;
                case '}': tokenKind = TokenKind.RBRACE; break;
                case ':': tokenKind = TokenKind.COLON; break;
                case '[': tokenKind = TokenKind.LBRACKET; break;
                case ']': tokenKind = TokenKind.RBRACKET; break;
                case ';': tokenKind = TokenKind.SEMICOLON; seenEquals = false; break;
                //@formatter:on
                default: {
                    // "c" is something we don't know about!
                    tokenKind = TokenKind.ERROR;
                    value = "Unexpected token '" + ((char) c) + "': invalid token";
                    break;
                }
            }

            if (c1 == -2) {
                // if token kind was known and the token didn't
                // check for an extra character
                read();
            }

            return new Token(tokenKind, value, start, cursor.index());
        }
    }

    private int read() throws IOException {
        if (pushback.length() > 0) {
            char c = pushback.charAt(0);
            pushback.deleteCharAt(0);
            cursor.push(c);
            next = c;
            return c;
        }
        int c = reader.read();
        cursor.push(c);
        next = c;
        return c;
    }

    private void unread(String s) {
        pushback.insert(0, s);
    }
}
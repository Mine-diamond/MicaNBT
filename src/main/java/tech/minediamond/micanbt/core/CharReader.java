package tech.minediamond.micanbt.core;

public class CharReader {
    private final char[] buffer;
    private final int length;
    private int cursor;

    public CharReader(String text) {
        this.buffer = text.toCharArray();
        this.length = buffer.length;
        this.cursor = 0;
    }

    public CharReader(char[] chars) {
        this.buffer = chars;
        this.length = chars.length;
        this.cursor = 0;
    }

    public char peek() {
        return buffer[cursor];
    }

    public char peek(int offset) {
        return buffer[cursor + offset];
    }

    public boolean peekOrConsume(char c) {
        if (peek() == c) {
            skip();
            return true;
        } else {
            return false;
        }
    }

    public char get(int index) {
        return buffer[index];
    }

    public char consume() {
        return buffer[cursor++];
    }

    public void skip() {
        cursor += 1;
    }

    public void skip(int n) {
        cursor += n;
    }

    public void skipOrThrow(char c) {
        char actual = consume();
        if (actual != c) {
            throw new SyntaxException("Expected " + c + " but got " + actual);
        }
    }

    public void skipEmptyChar() {
        while (hasRemaining()) {
            if (Tokens.isFormatChar(peek())) {
                skip();
            } else {
                return;
            }
        }
    }

    public String readUntil(char c) {
        int index = cursor;
        int count = 0;
        while (isAvailable(index + count) && get(index + count) != c) {
            count++;
        }
        skip(count);
        skipOrThrow(c);
        return substring(index, count);
    }

    public String readUntil(int initialDepth , char openChar, char closeChar) {
        int start = cursor;
        int depth = initialDepth;
        int currentPos = cursor;

        while (isAvailable(currentPos)) {
            char c = get(currentPos);

            if (c == openChar) {
                depth++;
            } else if (c == closeChar) {
                depth--;
            }

            if (depth == 0) {
                int length = currentPos - start;
                skip(length);
                skipOrThrow(closeChar);
                return substring(start, length);
            }

            currentPos++;
        }

        throw new SyntaxException("Missing closing character: " + closeChar);
    }

    public boolean hasRemaining() {
        return cursor < length;
    }

    public boolean isAvailable(int index) {
        return index < length;
    }

    public int position() {
        return cursor;
    }

    public void position(int newPos) {
        this.cursor = newPos;
    }

    public String substring(int start, int count) {
        return new String(buffer, start, count);
    }

    public String getErrorContext(String message) {
        if (cursor > length) {
            cursor = length;
        }
        int start = Math.max(0, cursor - 49);
        int count = cursor == buffer.length ? cursor - start : cursor - start + 1;

        if (cursor >= 50) {
            return message + "\n..." + new String(buffer, start, count) + " <- here";
        } else {
            return message + "\n" + new String(buffer, start, count) + " <- here";
        }
    }
}

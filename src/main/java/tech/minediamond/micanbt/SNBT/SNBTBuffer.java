package tech.minediamond.micanbt.SNBT;

public class SNBTBuffer {
    private final char[] buffer;
    private final int length;
    private int cursor;

    public SNBTBuffer(String text) {
        this.buffer = text.toCharArray();
        this.length = buffer.length;
        this.cursor = 0;
    }

    public SNBTBuffer(char[] chars) {
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
        if (consume() != c) {
            throw new SNBTParseException("Expected " + c + " but got " + consume());
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

    public boolean hasRemaining() {
        return cursor < length;
    }

    public int position() {
        return cursor;
    }

    public void position(int newPos) {
        this.cursor = newPos;
    }

    public String getErrorContext() {
        if (cursor > length) {
            cursor = length;
        }
        int start = Math.max(0, cursor - 49);
        int count = cursor - start + 1;

        if (cursor >= 50) {
            return "Error while parsing SNBTText\n..." + new String(buffer, start, count) + " <- here";
        } else {
            return "Error while parsing SNBTText\n" + new String(buffer, start, count) + " <- here";
        }
    }
}

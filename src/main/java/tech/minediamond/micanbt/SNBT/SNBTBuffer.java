package tech.minediamond.micanbt.SNBT;

import java.nio.BufferUnderflowException;
import java.nio.CharBuffer;

/**
 * 专门处理 SNBT 字符缓冲区的导航和操作
 */
public class SNBTBuffer {
    private final CharBuffer charBuffer;

    public SNBTBuffer(String text) {
        this.charBuffer = CharBuffer.wrap(text);
    }

    public char peek() {
        return charBuffer.get(charBuffer.position());
    }

    public char peek(int offset) {
        int target = charBuffer.position() + offset;
        if (target >= charBuffer.limit()) {
            throw new BufferUnderflowException();
        }
        return charBuffer.get(target);
    }

    public char consume() {
        return charBuffer.get();
    }

    public void skip() {
        charBuffer.position(charBuffer.position() + 1);
    }

    public void skip(int n) {
        charBuffer.position(charBuffer.position() + n);
    }

    public void skipEmptyChar() {
        while (charBuffer.hasRemaining()) {
            char c = consume();
            if (!Tokens.isFormatChar(c)) {
                charBuffer.position(charBuffer.position() - 1);
                return;
            }
        }
    }

    public boolean hasRemaining() {
        return charBuffer.hasRemaining();
    }

    public int position() {
        return charBuffer.position();
    }

    public void position(int newPos) {
        charBuffer.position(newPos);
    }

    public int limit() {
        return charBuffer.limit();
    }

    /**
     * 获取用于异常提示的错误上下文文本
     */
    public String getErrorContext() {
        int pos = charBuffer.position();
        CharBuffer view = charBuffer.duplicate();
        view.position(0);
        if (charBuffer.hasRemaining()) {
            view.limit(pos + 1);
        } else {
            view.limit(pos);
        }

        if (view.length() > 50) {
            return "Error while parsing SNBTText\n..." + view.subSequence(view.length() - 50, view.length()) + " <- here";
        } else {
            return "Error while parsing SNBTText\n" + view.subSequence(0, view.length()) + " <- here";
        }
    }

    // 用于调试
    public String fromStartToPosition() {
        int pos = charBuffer.position();
        CharBuffer view = charBuffer.duplicate();
        view.position(0);
        view.limit(pos);
        return view.toString();
    }

    private void printlnFromStartToPosition(String msg) { // for debug use
        System.out.println("custom message: " + msg + "\ncurrent processed: " + fromStartToPosition());
    }

    private void printlnFromStartToPosition() { // for debug use
        System.out.println("current processed: " + fromStartToPosition());
    }
}

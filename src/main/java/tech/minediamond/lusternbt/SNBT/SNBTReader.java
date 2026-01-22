package tech.minediamond.lusternbt.SNBT;

import tech.minediamond.lusternbt.tag.builtin.*;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class SNBTReader {
    private final CharBuffer charBuffer;
    private final Tag tag;

    private final StringBuilder reusableBuilder = new StringBuilder();

    public SNBTReader(String SNBTText) {
        this.charBuffer = CharBuffer.wrap(SNBTText);
        try {
            tag = parseTag("");
        } catch (Exception e) {
            if (charBuffer.position() >= 40) {
                throw new SNBTParseException("Error while parsing SNBTText\n..." + charBuffer.subSequence(charBuffer.position() - 40, charBuffer.position()) + " <- here", e);
            } else {
                throw new SNBTParseException("Error while parsing SNBTText\n" + charBuffer.subSequence(0, charBuffer.position()) + " <- here", e);
            }
        }
    }

    public SNBTReader(Path path) throws IOException {
        this.charBuffer = CharBuffer.wrap(Files.readString(path));
        try {
            tag = parseTag("");
        } catch (Exception e) {
            if (charBuffer.position() >= 40) {
                throw new SNBTParseException("Error while parsing SNBTText\n..." + charBuffer.subSequence(charBuffer.position() - 40, charBuffer.position()) + " <- here", e);
            } else {
                throw new SNBTParseException("Error while parsing SNBTText\n" + charBuffer.subSequence(0, charBuffer.position()) + " <- here", e);
            }
        }
    }

    public Tag getTag() {
        return tag;
    }

    private static String fromStartToPosition(CharBuffer buf) { // for debug use
        int pos = buf.position();
        CharBuffer view = buf.duplicate();
        view.position(0);
        view.limit(pos);
        return view.toString();
    }

    private void printlnFromStartToPosition(String msg) { // for debug use
        System.out.println("custom message: " + msg + "\ncurrent processed: " + fromStartToPosition(charBuffer));
    }

    private void printlnFromStartToPosition() { // for debug use
        System.out.println("current processed: " + fromStartToPosition(charBuffer));
    }

    public void skipEmptyChar() {
        char c;
        while (charBuffer.hasRemaining()) {
            c = this.charBuffer.get();
            if (!Tokens.isFormatChar(c)) {
                charBuffer.position(this.charBuffer.position() - 1);
                return;
            }
        }
    }

    private char peek() {
        return charBuffer.get(charBuffer.position());
    }

    private char peek(int offset) {
        int target = charBuffer.position() + offset;
        if (target >= charBuffer.limit()) {
            throw new BufferUnderflowException();
        }
        return charBuffer.get(target);
    }

    private char consume() {
        return charBuffer.get();
    }

    private void skip(int n) {
        charBuffer.position(charBuffer.position() + n);
    }

    private Tag parseTag(String name) {
        skipEmptyChar();
        char c = peek();
        return switch (c) {
            case Tokens.COMPOUND_BEGIN -> parseCompound(name);
            case Tokens.ARRAY_BEGIN -> parseArrayOrList(name);
            default -> parsePrimitive(name);
        };
    }

    private Tag parseArrayOrList(String name) {
        if (peek(1) == Tokens.ARRAY_END) {
            skip(2); // `[]`
            return new ListTag(name);
        }
        if (peek(2) == Tokens.ARRAY_SIGNATURE_SEPARATOR && "BIL".indexOf(peek(1)) != -1) {
            return parseTypedArray(peek(1), name);
        } else {
            return parseList(name);
        }
    }

    private Tag parseCompound(String name) {
        CompoundTag compoundTag = new CompoundTag(name);
        consume(); // `{`
        if (peek() == Tokens.COMPOUND_END) {
            consume(); // `}`
            return compoundTag;
        }

        while (peek() != Tokens.COMPOUND_END) {
            String subName = parseString();
            skipEmptyChar(); // empty char between `"` and `:`
            consume(); // `:`
            compoundTag.put(parseTag(subName));
            if (peek() == Tokens.VALUE_SEPARATOR) {
                consume(); // `,`
            }
            skipEmptyChar(); // skip empty char after `,` or something possible
        }

        consume(); // `}`
        return compoundTag;
    }

    private Tag parseTypedArray(char type, String name) {
        return switch (type) {
            case Tokens.TYPE_BYTE_UPPER -> parseByteArray(name);
            case Tokens.TYPE_INT_UPPER -> parseIntArray(name);
            case Tokens.TYPE_LONG_UPPER -> parseLongArray(name);
            default -> throw new IllegalStateException("Unexpected token: " + type);
        };
    }

    private ByteArrayTag parseByteArray(String name) {
        ByteArrayTag byteArrayTag = new ByteArrayTag(name);
        skip(3); // `[B;`
        skipEmptyChar(); // skip any possible empty char
        if (peek() == Tokens.ARRAY_END) {
            consume(); // `]`
            return byteArrayTag;
        }
        ArrayList<Byte> bytes = new ArrayList<>();
        while (peek() != Tokens.ARRAY_END) {
            String value = parseUnquotedString();
            if (value.endsWith("b") || value.endsWith("B")) {
                value = value.substring(0, value.length() - 1);
            }
            bytes.add(Byte.parseByte(value));
            if (peek() == Tokens.VALUE_SEPARATOR) {
                consume(); // `,`
            }
            skipEmptyChar(); // skip empty char after `,` or something possible
        }
        consume(); // `]`
        byte[] byteArray = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            byteArray[i] = bytes.get(i);
        }
        byteArrayTag.setValue(byteArray);
        return byteArrayTag;
    }

    private IntArrayTag parseIntArray(String name) {
        IntArrayTag intArrayTag = new IntArrayTag(name);
        skip(3); // `[I;`
        skipEmptyChar(); // skip any possible empty char
        if (peek() == Tokens.ARRAY_END) {
            consume(); // `]`
            return intArrayTag;
        }
        ArrayList<Integer> integers = new ArrayList<>();
        while (peek() != Tokens.ARRAY_END) {
            String value = parseUnquotedString();
            if (value.endsWith("i") || value.endsWith("I")) {
                value = value.substring(0, value.length() - 1);
            }
            integers.add(Integer.parseInt(value));
            if (peek() == Tokens.VALUE_SEPARATOR) {
                consume(); // `,`
            }
            skipEmptyChar(); // skip empty char after `,` or something possible
        }
        consume(); // `]`
        int[] intArray = new int[integers.size()];
        for (int i = 0; i < integers.size(); i++) {
            intArray[i] = integers.get(i);
        }
        intArrayTag.setValue(intArray);
        return intArrayTag;
    }

    private LongArrayTag parseLongArray(String name) {
        LongArrayTag longArrayTag = new LongArrayTag(name);
        skip(3); // `[L;`
        if (peek() == Tokens.ARRAY_END) {
            consume(); // `]`
            return longArrayTag;
        }
        ArrayList<Long> longs = new ArrayList<>();
        while (peek() != Tokens.ARRAY_END) {
            String value = parseUnquotedString();
            if (value.endsWith("l") || value.endsWith("L")) {
                value = value.substring(0, value.length() - 1);
            }
            longs.add(Long.parseLong(value));
            if (peek() == Tokens.VALUE_SEPARATOR) {
                consume(); // `,`
            }
            skipEmptyChar(); // skip empty char after `,` or something possible
        }
        consume(); // `]`
        long[] longArray = new long[longs.size()];
        for (int i = 0; i < longs.size(); i++) {
            longArray[i] = longs.get(i);
        }
        longArrayTag.setValue(longArray);
        return longArrayTag;
    }

    private ListTag parseList(String name) {
        ListTag listTag = new ListTag(name);
        consume(); //`[`
        if (peek() == Tokens.ARRAY_END) {
            consume(); // `]`
            return listTag;
        }
        while (peek() != Tokens.ARRAY_END) {
            listTag.add(parseTag(""));
            if (peek() == Tokens.VALUE_SEPARATOR) {
                consume(); // `,`
            }
            skipEmptyChar(); // skip empty char after `,` or something possible
        }
        consume(); // `]`
        return listTag;
    }

    private Tag parsePrimitive(String name) {
        skipEmptyChar();
        char firstChar = peek();
        if (firstChar == Tokens.DOUBLE_QUOTE || firstChar == Tokens.SINGLE_QUOTE) {
            return new StringTag(name, parseQuotedString());
        }
        String value = parseUnquotedString();

        if (value.equalsIgnoreCase("true")) return new ByteTag(name, (byte) 1);
        if (value.equalsIgnoreCase("false")) return new ByteTag(name, (byte) 0);

        char lastChar = value.charAt(value.length() - 1);
        char suffix = Character.toLowerCase(lastChar);
        try {
            if (Tokens.mayNumber(value)) {
                if (value.contains(".") || value.toLowerCase().contains("e")) {
                    return new DoubleTag(name, Double.parseDouble(value));
                } else {
                    return new IntTag(name, Integer.parseInt(value));
                }
            }

            String numPart = value.substring(0, value.length() - 1);
            return switch (suffix) {
                case Tokens.TYPE_BYTE -> new ByteTag(name, Byte.parseByte(numPart));
                case Tokens.TYPE_SHORT -> new ShortTag(name, Short.parseShort(numPart));
                case Tokens.TYPE_INT -> new IntTag(name, Integer.parseInt(numPart));
                case Tokens.TYPE_LONG -> new LongTag(name, Long.parseLong(numPart));
                case Tokens.TYPE_FLOAT -> new FloatTag(name, Float.parseFloat(numPart));
                case Tokens.TYPE_DOUBLE -> new DoubleTag(name, Double.parseDouble(numPart));
                default -> new StringTag(name, value); // Unquoted string
            };
        } catch (NumberFormatException e) {
            return new StringTag(name, value); // So this is unquoted string
        }
    }

    private String parseString() {
        skipEmptyChar();
        char firstChar = peek();

        if (firstChar == Tokens.DOUBLE_QUOTE || firstChar == Tokens.SINGLE_QUOTE) {
            return parseQuotedString();
        } else {
            return parseUnquotedString();
        }
    }

    private String parseQuotedString() {
        skipEmptyChar();
        char quoteChar = consume(); // `"` or `'`
        reusableBuilder.setLength(0);

        while (charBuffer.hasRemaining()) {
            char c = consume();

            if (c == Tokens.ESCAPE_MARKER) {
                if (!charBuffer.hasRemaining()) {
                    throw new RuntimeException("Unexpected end of SNBT: trailing backslash");
                }
                char next = consume();

                switch (next) {
                    case 'n' -> reusableBuilder.append('\n');
                    case 't' -> reusableBuilder.append('\t');
                    case 'r' -> reusableBuilder.append('\r');
                    case '\\' -> reusableBuilder.append('\\');
                    case '"' -> reusableBuilder.append('"');
                    case '\'' -> reusableBuilder.append('\'');
                    default -> reusableBuilder.append(next);
                }
            } else if (c == quoteChar) {
                return reusableBuilder.toString();
            } else {
                reusableBuilder.append(c);
            }
        }

        throw new RuntimeException("Unclosed quoted string");
    }

    private String parseUnquotedString() {
        skipEmptyChar();
        reusableBuilder.setLength(0);
        while (charBuffer.hasRemaining()) {
            char c = consume();
            if (Tokens.isAllowedInUnquotedString(c)) {
                reusableBuilder.append(c);
            } else {
                charBuffer.position(charBuffer.position() - 1);
                break;
            }
        }

        String result = reusableBuilder.toString();
        if (result.isEmpty()) {
            throw new RuntimeException("Expected an unquoted key but found none.");
        }
        return result;
    }
}

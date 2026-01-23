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
            tag = readRoot();
        } catch (Exception e) {
            throw new SNBTParseException(getSNBTParseExceptionText(), e);
        }
    }

    public SNBTReader(Path path) throws IOException {
        this.charBuffer = CharBuffer.wrap(Files.readString(path));
        try {
            tag = readRoot();
        } catch (Exception e) {
            throw new SNBTParseException(getSNBTParseExceptionText(), e);
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

    private String getSNBTParseExceptionText() {
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

    private Tag readRoot() {
        skipEmptyChar();
        int pos = charBuffer.position();
        char first = peek();
        if (first == Tokens.COMPOUND_BEGIN || first == Tokens.ARRAY_BEGIN) {
            return parseTag("");
        }
        String name = parseString();
        skipEmptyChar();
        if (!charBuffer.hasRemaining() || peek() != Tokens.COMPOUND_KEY_VALUE_SEPARATOR) {
            charBuffer.position(pos);
            return parseTag("");
        }
        skip(); //`:`
        return parseTag(name);
    }

    private Tag parseTag(String name) {
        skipEmptyChar();
        return switch (peek()) {
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

    // This implementation has a flaw:
    // if there is not a `,` between elements, and the parsing of sub-elements is correct,
    // it can still parse successfully, such as in {subCompoundTag: {}str:"str"}
    private Tag parseCompound(String name) {
        CompoundTag compoundTag = new CompoundTag(name);
        skip(); // `{`
        if (peek() == Tokens.COMPOUND_END) {
            skip(); // `}`
            return compoundTag;
        }

        while (peek() != Tokens.COMPOUND_END) {
            String subName = parseString();
            skipEmptyChar(); // empty char between `"` and `:`
            skip(); // `:`
            compoundTag.put(parseTag(subName));
            if (peek() == Tokens.VALUE_SEPARATOR) {
                skip(); // `,`
            }
            skipEmptyChar(); // skip empty char after `,` or something possible
        }

        skip(); // `}`
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
            skip(); // `]`
            return byteArrayTag;
        }
        ArrayList<Byte> bytes = new ArrayList<>();
        while (peek() != Tokens.ARRAY_END) {
            bytes.add((byte) parseArrayNumber(parseUnquotedString(), Byte.MIN_VALUE, Byte.MAX_VALUE, 0, 256, 1));
            if (peek() == Tokens.VALUE_SEPARATOR) {
                skip(); // `,`
            }
            skipEmptyChar(); // skip empty char after `,` or something possible
        }
        skip(); // `]`
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
            skip(); // `]`
            return intArrayTag;
        }
        ArrayList<Integer> integers = new ArrayList<>();
        while (peek() != Tokens.ARRAY_END) {
            integers.add((int) parseArrayNumber(parseUnquotedString(), Integer.MIN_VALUE, Integer.MAX_VALUE, 0, Integer.MAX_VALUE * 2L, 0));
            if (peek() == Tokens.VALUE_SEPARATOR) {
                skip(); // `,`
            }
            skipEmptyChar(); // skip empty char after `,` or something possible
        }
        skip(); // `]`
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
            skip(); // `]`
            return longArrayTag;
        }
        ArrayList<Long> longs = new ArrayList<>();
        while (peek() != Tokens.ARRAY_END) {
            longs.add(parseArrayNumber(parseUnquotedString(), Long.MIN_VALUE, Long.MAX_VALUE, 0, Long.MAX_VALUE, 1));
            if (peek() == Tokens.VALUE_SEPARATOR) {
                skip(); // `,`
            }
            skipEmptyChar(); // skip empty char after `,` or something possible
        }
        skip(); // `]`
        long[] longArray = new long[longs.size()];
        for (int i = 0; i < longs.size(); i++) {
            longArray[i] = longs.get(i);
        }
        longArrayTag.setValue(longArray);
        return longArrayTag;
    }

    // This implementation has a flaw:
    // if there is not a `,` between elements, and the parsing of sub-elements is correct,
    // it can still parse successfully, such as in [{str1:"str"}{str2:"str"}]
    private ListTag parseList(String name) {
        ListTag listTag = new ListTag(name);
        skip(); //`[`
        if (peek() == Tokens.ARRAY_END) {
            skip(); // `]`
            return listTag;
        }
        while (peek() != Tokens.ARRAY_END) {
            listTag.add(parseTag(""));
            if (peek() == Tokens.VALUE_SEPARATOR) {
                skip(); // `,`
            }
            skipEmptyChar(); // skip empty char after `,` or something possible
        }
        skip(); // `]`
        return listTag;
    }

    private Tag parsePrimitive(String name) {
        skipEmptyChar();
        if (!Tokens.isDigit(peek())) {
            return new StringTag(name, parseString());
        }

        String value = parseUnquotedString();
        if (value.equals("true")) return new ByteTag(name, (byte) 1);
        if (value.equals("false")) return new ByteTag(name, (byte) 0);

        char lastChar = value.charAt(value.length() - 1);
        char suffix = Character.toLowerCase(lastChar);

        boolean isSignedDefault = true;
        boolean isSigned = false;
        int radix = 10;
        int prefixNum = 0;
        int suffixNum = 1;

        try {
            if (value.startsWith("0x") || value.startsWith("0X")) {
                prefixNum = 2;
                radix = 16;
            }
            if ((value.startsWith("0b") || value.startsWith("0B")) && value.length() >= 4) {
                prefixNum = 2;
                radix = 2;
            }

            try {
                char lastChar2 = value.charAt(value.length() - 2);
                if (lastChar2 == 'u' || lastChar2 == 'U') {
                    suffixNum = 2;
                    isSignedDefault = false;
                    isSigned = false;
                }
                if (lastChar2 == 's' || lastChar2 == 'S') {
                    suffixNum = 2;
                    isSignedDefault = false;
                    isSigned = true;
                }
            } catch (Exception ignored) {
            }

            if (isSignedDefault) {
                isSigned = radix != 16 && radix != 2;
            }

            if (Tokens.mayNumber(value)) {
                if (value.contains(".") || value.toLowerCase().contains("e")) {
                    return new DoubleTag(name, Double.parseDouble(value));
                } else {
                    return new IntTag(name, Integer.parseInt(value));
                }
            }

            String numPart = value.substring(prefixNum, value.length() - suffixNum);

            if (isSigned) {
                return getSignedTag(name, value, suffix, radix, numPart);
            } else {
                return getUnSignedTag(name, value, suffix, radix, numPart);
            }

        } catch (NumberFormatException e) {
            if (e.getMessage().contains("Value out of range.")) {
                throw e;
            }
            return new StringTag(name, value); // So this is unquoted string
        }
    }

    private Tag getSignedTag(String name, String value, char suffix, int radix, String numPart) {
        return switch (suffix) {
            case Tokens.TYPE_BYTE -> new ByteTag(name, Byte.parseByte(numPart, radix));
            case Tokens.TYPE_SHORT -> new ShortTag(name, Short.parseShort(numPart, radix));
            case Tokens.TYPE_INT -> new IntTag(name, Integer.parseInt(numPart, radix));
            case Tokens.TYPE_LONG -> new LongTag(name, Long.parseLong(numPart, radix));
            case Tokens.TYPE_FLOAT -> new FloatTag(name, Float.parseFloat(numPart));
            case Tokens.TYPE_DOUBLE -> new DoubleTag(name, Double.parseDouble(numPart));
            case '0', '1', '2', '3', '4', '5', '6', '7', '8','9' -> new IntTag(name, Integer.parseInt(numPart, radix));
            default -> new StringTag(name, value); // Unquoted string
        };
    }

    private Tag getUnSignedTag(String name, String value, char suffix, int radix, String numPart) {
        return switch (suffix) {
            case Tokens.TYPE_BYTE -> {
                int b = Integer.parseInt(numPart, radix);
                if (b >= 256 || b <= -1)
                    throw new NumberFormatException("Value out of range. Value:\"" + value + "\"" + " Radix:" + radix);
                yield new ByteTag(name, (byte) b);
            }
            case Tokens.TYPE_SHORT -> {
                int s = Integer.parseInt(numPart, radix);
                if (s >= 65535 || s < -1)
                    throw new NumberFormatException("Value out of range. Value:\"" + value + "\"" + " Radix:" + radix);
                yield new ShortTag(name, (short) s);
            }
            case Tokens.TYPE_INT, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> new IntTag(name, Integer.parseUnsignedInt(numPart, radix));
            case Tokens.TYPE_LONG -> new LongTag(name, Long.parseUnsignedLong(numPart, radix));
            default -> new StringTag(name, value); // Unquoted string
        };
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

    private long parseArrayNumber(String value, long min, long max, long unSignedMin, long unSignedMax, int defaultSuffixNum) {
        boolean isSignedDefault = true;
        boolean isSigned = false;
        int radix = 10;
        int prefixNum = 0;
        int suffixNum = defaultSuffixNum;

        String lowerValue = value.toLowerCase();
        if (lowerValue.startsWith("0x")) {
            prefixNum = 2;
            radix = 16;
        } else if (lowerValue.startsWith("0b")) {
            // Special handling for byte tags as 0b is a valid byte tag
            if (max > 255 || value.length() > 4) {
                prefixNum = 2;
                radix = 2;
            }
        }

        int len = value.length();
        if (len > 0 && lowerValue.endsWith("i")) { //Special handling for int tags
            suffixNum = 1;
        }

        try {
            if (len >= 2) {
                char signChar = Character.toLowerCase(value.charAt(len - 2));
                if (signChar == 'u') {
                    suffixNum = 2;
                    isSignedDefault = false;
                    isSigned = false;
                } else if (signChar == 's') {
                    suffixNum = 2;
                    isSignedDefault = false;
                    isSigned = true;
                }
            }
        } catch (Exception ignored) {
        }

        if (isSignedDefault) {
            isSigned = radix != 16 && radix != 2;
        }

        String numPart = value.substring(prefixNum, len - suffixNum);
        long result = isSigned ? Long.parseLong(numPart, radix) : Long.parseUnsignedLong(numPart, radix);

        if (isSigned) {
            if (result < min || result > max) {
                throw new NumberFormatException("Value out of range. Value:\"" + value + "\" Radix:" + radix);
            }
        } else {
            if (result < unSignedMin || result > unSignedMax) {
                throw new NumberFormatException("Value out of range. Value:\"" + value + "\" Radix:" + radix);
            }
        }

        return result;
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

    private void skip() {
        charBuffer.position(charBuffer.position() + 1);
    }

    private void skip(int n) {
        charBuffer.position(charBuffer.position() + n);
    }

    public void skipEmptyChar() {
        char c;
        while (charBuffer.hasRemaining()) {
            c = consume();
            if (!Tokens.isFormatChar(c)) {
                charBuffer.position(charBuffer.position() - 1);
                return;
            }
        }
    }
}

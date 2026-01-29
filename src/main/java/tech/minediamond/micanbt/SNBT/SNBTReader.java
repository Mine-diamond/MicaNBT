package tech.minediamond.micanbt.SNBT;

import tech.minediamond.micanbt.SNBT.primitiveArray.ByteArray;
import tech.minediamond.micanbt.SNBT.primitiveArray.IntArray;
import tech.minediamond.micanbt.SNBT.primitiveArray.LongArray;
import tech.minediamond.micanbt.tag.builtin.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SNBTReader {
    private final SNBTBuffer snbtBuffer;
    private final Tag tag;

    private int depth = 0;
    private final StringBuilder reusableBuilder = new StringBuilder();

    public SNBTReader(String SNBTText) {
        this.snbtBuffer = new SNBTBuffer(SNBTText);
        try {
            tag = readRoot();
        } catch (Exception e) {
            throw new SNBTParseException(snbtBuffer.getErrorContext(), e);
        }
    }

    public SNBTReader(Path path) throws IOException {
        this.snbtBuffer = new SNBTBuffer(Files.readString(path));
        try {
            tag = readRoot();
        } catch (Exception e) {
            throw new SNBTParseException(snbtBuffer.getErrorContext(), e);
        }
    }

    public Tag getTag() {
        return tag;
    }

    private Tag readRoot() {
        snbtBuffer.skipEmptyChar();
        int pos = snbtBuffer.position();
        char first = snbtBuffer.peek();
        if (first == Tokens.COMPOUND_BEGIN || first == Tokens.ARRAY_BEGIN) {
            return parseTag("");
        }
        String name = parseString();
        snbtBuffer.skipEmptyChar();
        if (!snbtBuffer.hasRemaining() || snbtBuffer.peek() != Tokens.COMPOUND_KEY_VALUE_SEPARATOR) {
            snbtBuffer.position(pos);
            return parseTag("");
        }
        snbtBuffer.skip(); //`:`
        return parseTag(name);
    }

    private Tag parseTag(String name) {
        snbtBuffer.skipEmptyChar();
        return switch (snbtBuffer.peek()) {
            case Tokens.COMPOUND_BEGIN -> parseCompound(name);
            case Tokens.ARRAY_BEGIN -> parseArrayOrList(name);
            default -> parsePrimitive(name);
        };
    }

    private Tag parseArrayOrList(String name) {
        if (snbtBuffer.peek(1) == Tokens.ARRAY_END) {
            snbtBuffer.skip(2); // `[]`
            return new ListTag<>(name);
        }
        if (snbtBuffer.peek(2) == Tokens.ARRAY_SIGNATURE_SEPARATOR && "BIL".indexOf(snbtBuffer.peek(1)) != -1) {
            return parseTypedArray(snbtBuffer.peek(1), name);
        } else {
            return parseList(name);
        }
    }

    private Tag parseCompound(String name) {
        depth++;
        if (depth > Tokens.MAX_NESTING_DEPTH) {
            throw new SNBTParseException("max nesting depth exceeded");
        }
        CompoundTag compoundTag = new CompoundTag(name);
        snbtBuffer.skipOrThrow(Tokens.COMPOUND_BEGIN); // `{`
        snbtBuffer.skipEmptyChar();
        if (snbtBuffer.peekOrConsume(Tokens.COMPOUND_END)) { // `}`
            return compoundTag;
        }

        while (snbtBuffer.peek() != Tokens.COMPOUND_END) {
            String subName = parseString();
            snbtBuffer.skipEmptyChar(); // empty char between `"` and `:`
            snbtBuffer.skipOrThrow(Tokens.COMPOUND_KEY_VALUE_SEPARATOR); // `:`
            compoundTag.put(parseTag(subName));
            if (!snbtBuffer.peekOrConsume(Tokens.VALUE_SEPARATOR)) {// `,`
                break;
            }
            snbtBuffer.skipEmptyChar(); // skip empty char after `,` or something possible
        }
        snbtBuffer.skipEmptyChar();
        snbtBuffer.skipOrThrow(Tokens.COMPOUND_END); // `}`
        depth--;
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
        snbtBuffer.skip(3); // `[B;`
        snbtBuffer.skipEmptyChar(); // skip any possible empty char
        if (snbtBuffer.peekOrConsume(Tokens.ARRAY_END)) { // `]`
            return byteArrayTag;
        }
        ByteArray byteArray = new ByteArray();
        while (snbtBuffer.peek() != Tokens.ARRAY_END) {
            byteArray.add((byte) parseArrayNumber(parseUnquotedString(), Byte.MIN_VALUE, Byte.MAX_VALUE, 0, 255, 1));
            if (!snbtBuffer.peekOrConsume(Tokens.VALUE_SEPARATOR)) {// `,`
                break;
            }
            snbtBuffer.skipEmptyChar(); // skip empty char after `,` or something possible
        }

        snbtBuffer.skipOrThrow(Tokens.ARRAY_END); // `]`
        byteArrayTag.setValue(byteArray.toArray());
        return byteArrayTag;
    }

    private IntArrayTag parseIntArray(String name) {
        IntArrayTag intArrayTag = new IntArrayTag(name);
        snbtBuffer.skip(3); // `[I;`
        snbtBuffer.skipEmptyChar(); // skip any possible empty char
        if (snbtBuffer.peekOrConsume(Tokens.ARRAY_END)) { // `]`
            return intArrayTag;
        }
        IntArray intArray = new IntArray();
        while (snbtBuffer.peek() != Tokens.ARRAY_END) {
            intArray.add((int) parseArrayNumber(parseUnquotedString(), Integer.MIN_VALUE, Integer.MAX_VALUE, 0, Integer.MAX_VALUE * 2L + 1, 0));
            if (!snbtBuffer.peekOrConsume(Tokens.VALUE_SEPARATOR)) {// `,`
                break;
            }
            snbtBuffer.skipEmptyChar(); // skip empty char after `,` or something possible
        }

        snbtBuffer.skipOrThrow(Tokens.ARRAY_END); // `]`
        intArrayTag.setValue(intArray.toArray());
        return intArrayTag;
    }

    private LongArrayTag parseLongArray(String name) {
        LongArrayTag longArrayTag = new LongArrayTag(name);
        snbtBuffer.skip(3); // `[L;`
        if (snbtBuffer.peekOrConsume(Tokens.ARRAY_END)) { // `]`
            return longArrayTag;
        }
        LongArray longArray = new LongArray();
        while (snbtBuffer.peek() != Tokens.ARRAY_END) {
            longArray.add(parseArrayNumber(parseUnquotedString(), Long.MIN_VALUE, Long.MAX_VALUE, Long.MIN_VALUE, Long.MAX_VALUE, 1));
            if (!snbtBuffer.peekOrConsume(Tokens.VALUE_SEPARATOR)) {// `,`
                break;
            }
            snbtBuffer.skipEmptyChar(); // skip empty char after `,` or something possible
        }

        snbtBuffer.skipOrThrow(Tokens.ARRAY_END); // `]`
        longArrayTag.setValue(longArray.toArray());
        return longArrayTag;
    }

    private ListTag<? extends Tag> parseList(String name) {
        depth++;
        if (depth > Tokens.MAX_NESTING_DEPTH) {
            throw new SNBTParseException("max nesting depth exceeded");
        }
        ListTag<Tag> listTag = new ListTag<>(name);
        snbtBuffer.skip(); //`[`
        snbtBuffer.skipEmptyChar();
        if (snbtBuffer.peekOrConsume(Tokens.ARRAY_END)) { // `]`
            return listTag;
        }
        while (snbtBuffer.peek() != Tokens.ARRAY_END) {
            listTag.add(parseTag(""));
            if (!snbtBuffer.peekOrConsume(Tokens.VALUE_SEPARATOR)) {// `,`
                break;
            }
            snbtBuffer.skipEmptyChar(); // skip empty char after `,` or something possible
        }
        snbtBuffer.skipEmptyChar();
        snbtBuffer.skipOrThrow(Tokens.ARRAY_END); // `]`
        depth--;
        return listTag;
    }

    private Tag parsePrimitive(String name) {
        snbtBuffer.skipEmptyChar();

        String value = parseString();
        if (value.equalsIgnoreCase(Tokens.LITERAL_TRUE)) return new ByteTag(name, (byte) 1);
        if (value.equalsIgnoreCase(Tokens.LITERAL_FALSE)) return new ByteTag(name, (byte) 0);

        if (value.isEmpty() || !Tokens.isDigit(value.charAt(0))) {
            return new StringTag(name, value);
        }

        char suffix = Character.toLowerCase(value.charAt(value.length() - 1));

        boolean isSignedDefault = true;
        boolean isSigned = false;
        int radix = 10;
        int prefixNum = 0;
        int suffixNum = 1;

        try {
            if (value.startsWith(Tokens.HEX_PREFIX) || value.startsWith(Tokens.HEX_PREFIX_UPPER)) {
                prefixNum = 2;
                radix = 16;
            }
            if ((value.startsWith(Tokens.BINARY_PREFIX) || value.startsWith(Tokens.BINARY_PREFIX_UPPER)) && value.length() >= 4) {
                prefixNum = 2;
                radix = 2;
            }

            try {
                char lastChar2 = value.charAt(value.length() - 2);
                if (lastChar2 == Tokens.TYPE_UNSIGNED || lastChar2 == Tokens.TYPE_UNSIGNED_UPPER) {
                    suffixNum = 2;
                    isSignedDefault = false;
                    isSigned = false;
                }
                if (lastChar2 == Tokens.TYPE_SIGNED || lastChar2 == Tokens.TYPE_SIGNED_UPPER) {
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
            case Tokens.TYPE_INT, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' ->
                    new IntTag(name, Integer.parseInt(numPart, radix));
            case Tokens.TYPE_LONG -> new LongTag(name, Long.parseLong(numPart, radix));
            case Tokens.TYPE_FLOAT -> new FloatTag(name, Float.parseFloat(numPart));
            case Tokens.TYPE_DOUBLE -> new DoubleTag(name, Double.parseDouble(numPart));
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
            case Tokens.TYPE_INT, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' ->
                    new IntTag(name, Integer.parseUnsignedInt(numPart, radix));
            case Tokens.TYPE_LONG -> new LongTag(name, Long.parseUnsignedLong(numPart, radix));
            default -> new StringTag(name, value); // Unquoted string
        };
    }

    private String parseString() {
        snbtBuffer.skipEmptyChar();
        char firstChar = snbtBuffer.peek();

        if (firstChar == Tokens.DOUBLE_QUOTE || firstChar == Tokens.SINGLE_QUOTE) {
            return parseQuotedString();
        } else {
            return parseUnquotedString();
        }
    }

    private String parseQuotedString() {
        snbtBuffer.skipEmptyChar();
        char quoteChar = snbtBuffer.consume(); // `"` or `'`
        reusableBuilder.setLength(0);

        while (snbtBuffer.hasRemaining()) {
            char c = snbtBuffer.consume();

            if (c == Tokens.ESCAPE_MARKER) {
                if (!snbtBuffer.hasRemaining()) {
                    throw new RuntimeException("Unexpected end of SNBT: trailing backslash");
                }
                char next = snbtBuffer.consume();

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
        snbtBuffer.skipEmptyChar();
        int startPos = snbtBuffer.position();
        int endPos = startPos;
        while (snbtBuffer.isAvailable(endPos)) {
            if (Tokens.isAllowedInUnquotedString(snbtBuffer.get(endPos))) {
                endPos++;
            } else {
                break;
            }
        }
        String substring = snbtBuffer.substring(startPos, endPos - startPos);
        if (substring.isEmpty()) {
            throw new RuntimeException("Expected an unquoted string but found none.");
        }
        snbtBuffer.position(endPos);
        return substring;
    }

    private long parseArrayNumber(String value, long min, long max, long unSignedMin, long unSignedMax, int defaultSuffixNum) {
        boolean isSignedDefault = true;
        boolean isSigned = false;
        int radix = 10;
        int prefixNum = 0;
        int suffixNum = defaultSuffixNum;

        if (value == null || value.isEmpty()) {
            throw new RuntimeException("Expected a non-empty string but found an empty string.");
        }

        char lastChar = value.charAt(value.length() - 1);
        int len = value.length();
        if (lastChar == Tokens.TYPE_INT || lastChar == Tokens.TYPE_INT_UPPER) { //Special handling for int tags
            suffixNum = 1;
        }

        if (len > 2) {
            if (value.charAt(0) == '0') {
                char secondChar = value.charAt(1);
                if (secondChar == 'x' || secondChar == 'X') {
                    prefixNum = 2;
                    radix = 16;
                } else if (secondChar == 'b' || secondChar == 'B') {
                    prefixNum = 2;
                    radix = 2;
                }
            }

            if (len - 2 >= prefixNum) {
                char signChar = value.charAt(len - 2);
                if (signChar == Tokens.TYPE_UNSIGNED || signChar == Tokens.TYPE_UNSIGNED_UPPER) {
                    suffixNum = 2;
                    isSignedDefault = false;
                    isSigned = false;
                } else if (signChar == Tokens.TYPE_SIGNED || signChar == Tokens.TYPE_SIGNED_UPPER) {
                    suffixNum = 2;
                    isSignedDefault = false;
                    isSigned = true;
                }
            }
        }

        if (isSignedDefault) {
            isSigned = radix != 16 && radix != 2;
        }

        long result = isSigned ? Long.parseLong(value, prefixNum, len - suffixNum, radix) : Long.parseUnsignedLong(value, prefixNum, len - suffixNum, radix);

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
}

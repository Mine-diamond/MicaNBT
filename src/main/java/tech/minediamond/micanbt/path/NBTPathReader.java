package tech.minediamond.micanbt.path;

import tech.minediamond.micanbt.core.CharReader;
import tech.minediamond.micanbt.core.Tokens;

import java.util.ArrayList;
import java.util.List;

public class NBTPathReader {
    private final CharReader reader;
    private final List<String> tokens = new ArrayList<>();

    private final StringBuilder reusableBuilder = new StringBuilder();

    public NBTPathReader(String path) {
        this.reader = new CharReader(path);
        try {
            parsePath();
        } catch (Exception e) {
            throw new NBTPathParseException(reader.getErrorContext("Error while parsing NBT path"), e);
        }
    }

    public static String[] read(String path) {
        return new NBTPathReader(path).getTokensArray();
    }

    public List<String> getTokens() {
        return tokens;
    }

    public String[] getTokensArray() {
        return tokens.toArray(new String[0]);
    }

    private void parsePath() {
        while (reader.hasRemaining()) {
            switch (reader.peek()) {
                case '.' -> reader.skip();
                case '[' -> parseIndex();
                case '"', '\'' -> parseQuoted();
                default -> parseUnquoted();
            }
        }
    }

    private void parseIndex() {
        tokens.add(parseIntToken());
    }

    private void parseQuoted() {
        tokens.add(parseQuotedToken());
    }

    private void parseUnquoted() {
        tokens.add(parseUnquotedToken());
    }

    private String parseIntToken() {
        reader.skipOrThrow(Tokens.ARRAY_BEGIN); //[
        int index = reader.position();
        int count = 0;
        while (reader.isAvailable(index + count) && reader.get(index + count) != Tokens.ARRAY_END) {
            count++;
        }
        reader.skip(count);
        reader.skipOrThrow(Tokens.ARRAY_END);

        String value = reader.substring(index, count);
        tryParseInt(value);
        return value;
    }

    private void tryParseInt(String token) {
        try {
            Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw new NBTPathParseException("Token " + token + " is not an integer", e);
        }
    }

    private String parseQuotedToken() {
        char quoteChar = reader.consume(); // `"` or `'`
        reusableBuilder.setLength(0);

        while (reader.hasRemaining()) {
            char c = reader.consume();

            if (c == Tokens.ESCAPE_MARKER) {
                if (!reader.hasRemaining()) {
                    throw new RuntimeException("Unexpected end of NBTPath: trailing backslash");
                }
                char next = reader.consume();

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

        throw new NBTPathParseException("Unclosed quoted string");
    }

    private String parseUnquotedToken() {
        int startPos = reader.position();
        int endPos = startPos;
        while (reader.isAvailable(endPos)) {
            if (reader.get(endPos) != Tokens.DOT && reader.get(endPos) != Tokens.ARRAY_BEGIN) {
                endPos++;
            } else {
                break;
            }
        }
        String substring = reader.substring(startPos, endPos - startPos);
        if (substring.isEmpty()) {
            throw new NBTPathParseException("Expected an unquoted token but found none.");
        }
        reader.position(endPos);
        return substring;
    }
}

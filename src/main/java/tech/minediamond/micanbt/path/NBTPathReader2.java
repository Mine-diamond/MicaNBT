package tech.minediamond.micanbt.path;

import tech.minediamond.micanbt.SNBT.SNBT;
import tech.minediamond.micanbt.SNBT.SNBTParseException;
import tech.minediamond.micanbt.core.CharReader;
import tech.minediamond.micanbt.core.Tokens;
import tech.minediamond.micanbt.path.nbtpathtoken.*;
import tech.minediamond.micanbt.tag.CompoundTag;
import tech.minediamond.micanbt.tag.Tag;

import java.util.ArrayList;
import java.util.List;

public class NBTPathReader2 {
    private final CharReader reader;
    private final List<PathToken> tokens = new ArrayList<>();

    private final StringBuilder reusableBuilder = new StringBuilder();

    public NBTPathReader2(String path) {
        this.reader = new CharReader(path);
        try {
            parsePath();
        } catch (Exception e) {
            throw new NBTPathParseException(reader.getErrorContext("Error while parsing NBT path"), e);
        }
    }

    public static PathToken[] read(String path) {
        return new NBTPathReader2(path).getTokensArray();
    }

    public List<PathToken> getTokens() {
        return tokens;
    }

    public PathToken[] getTokensArray() {
        return tokens.toArray(new PathToken[0]);
    }

    private void parsePath() {
        while (reader.hasRemaining()) {
            switch (reader.peek()) {
                case Tokens.DOT -> reader.skip();
                case Tokens.ARRAY_BEGIN -> parseIndex();
                case Tokens.DOUBLE_QUOTE, Tokens.SINGLE_QUOTE -> parseQuoted();
                case Tokens.COMPOUND_BEGIN -> parseCompound();
                default -> parseUnquoted();
            }
        }
    }

    private void parseIndex() {
        tokens.add(parseIndexToken());
    }

    private void parseQuoted() {
        tokens.add(parseQuotedToken());
    }

    private void parseUnquoted() {
        tokens.add(parseUnquotedToken());
    }

    private void parseCompound() {
        tokens.add(parseFilterToken());
    }

    private PathToken parseIndexToken() {
        reader.skipOrThrow(Tokens.ARRAY_BEGIN); // [
        String value = reader.readUntil(Tokens.ARRAY_END);
        try {
            if (Tokens.mayNumber(value)) {
                int i = Integer.parseInt(value);
                return new IndexToken(i);
            }
        } catch (NumberFormatException ignored) {
        }
        try {
            if (SNBT.parse(value) instanceof CompoundTag compoundTag) {
                return new CompoundMatchToken(compoundTag);
            }
        } catch (SNBTParseException ignored) {
        }

        throw new NBTPathParseException("Data that does not conform to the format index");
    }

    private KeyToken parseQuotedToken() {
        char quoteChar = reader.consume(); // `"` or `'`
        reusableBuilder.setLength(0);

        while (reader.hasRemaining()) {
            char c = reader.consume();

            if (c == Tokens.ESCAPE_MARKER) {
                if (!reader.hasRemaining()) {
                    throw new NBTPathParseException("Unexpected end of NBTPath: trailing backslash");
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
                return new KeyToken(reusableBuilder.toString());
            } else {
                reusableBuilder.append(c);
            }
        }

        throw new NBTPathParseException("Unclosed quoted string");
    }

    private KeyToken parseUnquotedToken() {
        int startPos = reader.position();
        int endPos = startPos;
        while (reader.isAvailable(endPos)) {
            if (reader.get(endPos) != Tokens.DOT && reader.get(endPos) != Tokens.ARRAY_BEGIN && reader.get(endPos) != Tokens.COMPOUND_BEGIN) {
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
        return new KeyToken(substring);
    }

    private PathToken parseFilterToken() {
        reader.skipOrThrow(Tokens.COMPOUND_BEGIN);
        String compoundValue = reader.readUntil(1, Tokens.COMPOUND_BEGIN, Tokens.COMPOUND_END);
        compoundValue = "{" + compoundValue + "}";
        Tag tag = SNBT.parse(compoundValue);
        if (tag instanceof CompoundTag compoundTag) {
            return new FilterToken(compoundTag);
        }
        throw new NBTPathParseException();
    }
}

package tech.minediamond.micanbt.path;

import tech.minediamond.micanbt.SNBT.SNBT;
import tech.minediamond.micanbt.SNBT.SNBTStyle;
import tech.minediamond.micanbt.core.Tokens;
import tech.minediamond.micanbt.path.nbtpathtoken.*;

public class NBTPathWriter2 {
    private boolean isFirst = true;
    private final NBTPath2 path;
    private final StringBuilder builder;

    public NBTPathWriter2(NBTPath2 path) {
        this.path = path;
        this.builder = new StringBuilder();
        WriteTokens();
    }

    public static String getNBTPathString(NBTPath2 path) {
        return new NBTPathWriter2(path).getNBTPathString();
    }

    public String getNBTPathString() {
        return builder.toString();
    }

    public void WriteTokens() {
        for (PathToken token : path.getTokens()) {
            if (token instanceof KeyToken keyToken) {
                writeKeyToken(keyToken);
            } else if (token instanceof IndexToken indexToken) {
                writeIndexToken(indexToken);
            } else if (token instanceof CompoundMatchToken compoundMatchToken) {
                writeCompoundMatchToken(compoundMatchToken);
            } else if (token instanceof FilterToken filterToken) {
                writeFilterToken(filterToken);
            }
        }
    }

    public void writeKeyToken(KeyToken keyToken) {
        if (isFirst) {
            isFirst = false;
        } else {
            builder.append(Tokens.DOT);
        }
        boolean needQuotes = Tokens.needQuotationInNBTPath(keyToken.key());
        if (needQuotes) {
            builder.append(Tokens.DOUBLE_QUOTE);
        }
        escapeAndAppend(keyToken.asString());
        if (needQuotes) {
            builder.append(Tokens.DOUBLE_QUOTE);
        }
    }

    public void writeIndexToken(IndexToken indexToken) {
        builder.append(Tokens.ARRAY_BEGIN)
                .append(indexToken.index())
                .append(Tokens.ARRAY_END);
    }

    public void writeCompoundMatchToken(CompoundMatchToken compoundMatchToken) {
        builder.append(Tokens.ARRAY_BEGIN);
        builder.append(SNBT.stringify(compoundMatchToken.pattern(), false, SNBTStyle.COMPACT));
        builder.append(Tokens.ARRAY_END);
    }

    public void writeFilterToken(FilterToken filterToken) {
        builder.append(SNBT.stringify(filterToken.pattern(), false, SNBTStyle.COMPACT));
    }

    //All escape character supported by snbt and `"`
    private void escapeAndAppend(String input) {
        if (input == null || input.isEmpty()) return;

        int length = input.length();
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            switch (c) {
                case '\b' -> builder.append("\\b");
                case '\f' -> builder.append("\\f");
                case '\n' -> builder.append("\\n");
                case '\r' -> builder.append("\\r");
                case '\t' -> builder.append("\\t");
                case '\\' -> builder.append("\\\\");
                case '"' -> builder.append("\\\"");
                default -> builder.append(c);
            }
        }
    }
}

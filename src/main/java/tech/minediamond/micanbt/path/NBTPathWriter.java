package tech.minediamond.micanbt.path;

import tech.minediamond.micanbt.core.Tokens;

public class NBTPathWriter {
    private boolean isFirst = true;
    private final NBTPath path;
    private final StringBuilder builder;

    public NBTPathWriter(NBTPath path) {
        this.path = path;
        this.builder = new StringBuilder();
        WriteTokens();
    }

    public static String getNBTPathString(NBTPath path) {
        return new NBTPathWriter(path).getNBTPathString();
    }

    public String getNBTPathString() {
        return builder.toString();
    }

    public void WriteTokens() {
        for (Object token : path.tokens()) {
            if (token instanceof String stringToken) {
                writeStringToken(stringToken);
            } else if (token instanceof Integer integerToken) {
                writeIntToken(integerToken);
            }
        }
    }

    public void writeStringToken(String stringToken) {
        if (isFirst) {
            isFirst = false;
        } else {
            builder.append(Tokens.DOT);
        }
        boolean needQuotes = Tokens.needQuotationInNBTPath(stringToken);
        if (needQuotes) {
            builder.append(Tokens.DOUBLE_QUOTE);
        }
        escapeAndAppend(stringToken);
        if (needQuotes) {
            builder.append(Tokens.DOUBLE_QUOTE);
        }
    }

    public void writeIntToken(int integer) {
        builder.append(Tokens.ARRAY_BEGIN)
                .append(integer)
                .append(Tokens.ARRAY_END);
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

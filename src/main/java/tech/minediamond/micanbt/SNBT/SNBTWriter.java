package tech.minediamond.micanbt.SNBT;

import tech.minediamond.micanbt.tag.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SNBTWriter {
    private static final String[] INDENTS = new String[16];

    private final Tag tag;
    private final StringBuilder builder;
    private final SNBTStyle snbtStyle;
    private int depth = 0;

    static {
        INDENTS[0] = "";
        for (int i = 1; i < INDENTS.length; i++) {
            INDENTS[i] = INDENTS[i - 1] + "\t";
        }
    }

    public SNBTWriter(Tag tag, boolean includeRootName, SNBTStyle snbtStyle) {
        this.tag = tag;
        this.builder = new StringBuilder();
        this.snbtStyle = snbtStyle;
        if (includeRootName) {
            stringifyRootTagName();
        }
        stringify(tag);
    }

    public String getSNBTText() {
        return builder.toString();
    }

    public void write(Path path) throws IOException {
        Files.writeString(path, getSNBTText());
    }

    private void stringifyRootTagName() {
        stringifyString(tag.getName());
        builder.append(Tokens.COMPOUND_KEY_VALUE_SEPARATOR);
        if (snbtStyle != SNBTStyle.COMPACT) {
            builder.append(Tokens.SPACE);
        }
    }

    // For primitive data types, do not care the surrounding tabs;
    // CompoundTag/ListTag/ArrayTag should pay attention and fill in appropriate new lines and tabs for their subTags.
    private void stringify(Tag tag) {
        if (tag instanceof CompoundTag compoundTag) {
            stringifyCompoundTag(compoundTag);
        } else if (tag instanceof ListTag<?> listTag) {
            stringifyListTag(listTag);
        } else if (tag instanceof StringTag stringTag) {
            stringifyStringTag(stringTag);
        } else if (tag instanceof ByteArrayTag byteArrayTag) {
            stringifyByteArrayTag(byteArrayTag);
        } else if (tag instanceof IntArrayTag intArrayTag) {
            stringifyIntArrayTag(intArrayTag);
        } else if (tag instanceof LongArrayTag longArrayTag) {
            stringifyLongArrayTag(longArrayTag);
        } else if (tag instanceof ByteTag byteTag) {
            builder.append(byteTag.getValue()).append(Tokens.TYPE_BYTE);
        } else if (tag instanceof ShortTag shortTag) {
            builder.append(shortTag.getValue()).append(Tokens.TYPE_SHORT);
        } else if (tag instanceof IntTag intTag) {
            builder.append(intTag.getValue());
        } else if (tag instanceof LongTag longTag) {
            builder.append(longTag.getValue()).append(Tokens.TYPE_LONG_UPPER);
        } else if (tag instanceof FloatTag floatTag) {
            builder.append(floatTag.getValue()).append(Tokens.TYPE_FLOAT);
        } else if (tag instanceof DoubleTag doubleTag) {
            builder.append(doubleTag.getValue()).append(Tokens.TYPE_DOUBLE);
        }
    }

    private void stringifyCompoundTag(CompoundTag compoundTag) {
        builder.append(Tokens.COMPOUND_BEGIN);
        if (compoundTag.isEmpty()) {
            builder.append(Tokens.COMPOUND_END);
            return;
        }
        if (snbtStyle == SNBTStyle.INDENTED) {
            depth++;
            newLineAndAddTab();
        }
        boolean isFirst = true;
        for (Tag subTag : compoundTag) {
            if (isFirst) {
                isFirst = false;
            } else {
                addValueSeparator();
            }
            stringifyCompoundItem(subTag);
        }
        if (snbtStyle == SNBTStyle.INDENTED) {
            depth--;
            newLineAndAddTab();
        }
        builder.append(Tokens.COMPOUND_END);
    }

    private void stringifyCompoundItem(Tag subTag) {
        boolean needQuotation = Tokens.needQuotation(subTag.getName());
        if (needQuotation) {
            builder.append(Tokens.DOUBLE_QUOTE)
                    .append(subTag.getName())
                    .append(Tokens.DOUBLE_QUOTE);
        } else {
            builder.append(subTag.getName());
        }
        builder.append(Tokens.COMPOUND_KEY_VALUE_SEPARATOR);
        if (snbtStyle != SNBTStyle.COMPACT) {
            builder.append(Tokens.SPACE);
        }
        stringify(subTag);
    }

    private void stringifyListTag(ListTag<?> listTag) {
        builder.append(Tokens.ARRAY_BEGIN);
        boolean isFirst = true;
        if (snbtStyle == SNBTStyle.INDENTED) {
            depth++;
            newLineAndAddTab();
        }
        for (Tag subTag : listTag) {
            if (isFirst) {
                isFirst = false;
            } else {
                addValueSeparator();
            }

            stringify(subTag);
        }
        if (snbtStyle == SNBTStyle.INDENTED) {
            depth--;
            newLineAndAddTab();
        }
        builder.append(Tokens.ARRAY_END);
    }

    private void stringifyStringTag(StringTag stringTag) {
        stringifyString(stringTag.getValue());
    }

    private void stringifyString(String string) {
        builder.append(Tokens.DOUBLE_QUOTE);
        escapeAndAppend(string);
        builder.append(Tokens.DOUBLE_QUOTE);
    }

    private void stringifyByteArrayTag(ByteArrayTag byteArrayTag) {
        builder.append(Tokens.ARRAY_BEGIN)
                .append(Tokens.TYPE_BYTE_UPPER)
                .append(Tokens.ARRAY_SIGNATURE_SEPARATOR);
        if (byteArrayTag.getValue().length != 0) {
            for (byte b : byteArrayTag.getValue()) {
                if (snbtStyle != SNBTStyle.COMPACT) {
                    builder.append(Tokens.SPACE);
                }
                builder.append(b)
                        .append(Tokens.TYPE_BYTE)
                        .append(Tokens.VALUE_SEPARATOR);
            }
            builder.setLength(builder.length() - 1);
        }
        builder.append(Tokens.ARRAY_END);
    }

    private void stringifyIntArrayTag(IntArrayTag intArrayTag) {
        builder.append(Tokens.ARRAY_BEGIN)
                .append(Tokens.TYPE_INT_UPPER)
                .append(Tokens.ARRAY_SIGNATURE_SEPARATOR);
        if (intArrayTag.getValue().length != 0) {
            for (int i : intArrayTag.getValue()) {
                if (snbtStyle != SNBTStyle.COMPACT) {
                    builder.append(Tokens.SPACE);
                }
                builder.append(i)
                        .append(Tokens.VALUE_SEPARATOR);
            }
            builder.setLength(builder.length() - 1);
        }
        builder.append(Tokens.ARRAY_END);
    }

    private void stringifyLongArrayTag(LongArrayTag longArrayTag) {
        builder.append(Tokens.ARRAY_BEGIN)
                .append(Tokens.TYPE_LONG_UPPER)
                .append(Tokens.ARRAY_SIGNATURE_SEPARATOR);
        if (longArrayTag.getValue().length != 0) {
            for (long l : longArrayTag.getValue()) {
                if (snbtStyle != SNBTStyle.COMPACT) {
                    builder.append(Tokens.SPACE);
                }
                builder.append(l)
                        .append(Tokens.TYPE_LONG_UPPER)
                        .append(Tokens.VALUE_SEPARATOR);
            }
            builder.setLength(builder.length() - 1);
        }
        builder.append(Tokens.ARRAY_END);
    }

    private void addTab() {
        if (depth < INDENTS.length) {
            builder.append(INDENTS[depth]);
        } else {
            builder.append("\t".repeat(depth));
        }
    }

    private void newLineAndAddTab() {
        builder.append(Tokens.NEWLINE);
        addTab();
    }

    private void addValueSeparator() {
        switch (snbtStyle) {
            case COMPACT -> builder.append(Tokens.VALUE_SEPARATOR);
            case SPACED -> builder.append(Tokens.VALUE_SEPARATOR).append(Tokens.SPACE);
            case INDENTED -> {
                builder.append(Tokens.VALUE_SEPARATOR);
                newLineAndAddTab();
            }
        }
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

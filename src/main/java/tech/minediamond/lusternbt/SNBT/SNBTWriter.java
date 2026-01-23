package tech.minediamond.lusternbt.SNBT;

import tech.minediamond.lusternbt.tag.builtin.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SNBTWriter {
    private final Tag tag;
    private final StringBuilder builder;
    private final SNBTStyle snbtStyle;
    private int depth = 0;

    public SNBTWriter(Tag tag, boolean stringifyRootTagName, SNBTStyle snbtStyle) {
        this.tag = tag;
        this.builder = new StringBuilder();
        this.snbtStyle = snbtStyle;
        if (stringifyRootTagName) {
            stringifyRootTagName();
        }
        stringify(tag);
    }

    public String getSNBTString() {
        return builder.toString();
    }

    public void writeSNBT(Path path) throws IOException {
        Files.writeString(path, getSNBTString());
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
        } else if (tag instanceof ListTag listTag) {
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
        if (snbtStyle == SNBTStyle.INDENTED) {
            stringifyCompoundTagIfLineBreak(compoundTag);
        } else {
            stringifyCompoundTagIfNotLineBreak(compoundTag);
        }
    }

    private void stringifyCompoundTagIfLineBreak(CompoundTag compoundTag) {
        builder.append(Tokens.COMPOUND_BEGIN);
        if (!compoundTag.isEmpty()) {
            depth++;
            boolean isFirst = true;
            for (Tag subTag : compoundTag) {
                if (!isFirst) {
                    builder.append(Tokens.VALUE_SEPARATOR);
                } else {
                    isFirst = false;
                }
                newLineAndAddTab();
                stringifyCompoundItem(subTag);
            }
            depth--;
            newLineAndAddTab();
        }
        builder.append(Tokens.COMPOUND_END);
    }

    private void stringifyCompoundTagIfNotLineBreak(CompoundTag compoundTag) {
        builder.append(Tokens.COMPOUND_BEGIN);
        if (!compoundTag.isEmpty()) {
            boolean isFirst = true;
            for (Tag subTag : compoundTag) {
                if (!isFirst) {
                    builder.append(Tokens.VALUE_SEPARATOR);
                    if (snbtStyle == SNBTStyle.SPACED) {
                        builder.append(Tokens.SPACE);
                    }
                } else {
                    isFirst = false;
                }
                stringifyCompoundItem(subTag);
            }
        }
        builder.append(Tokens.COMPOUND_END);
    }

    private void stringifyCompoundItem(Tag subTag) {
        boolean needQuotation = Tokens.needQuotation(subTag.getName());
        if (needQuotation) {
            builder.append(Tokens.DOUBLE_QUOTE);
        }
        builder.append(subTag.getName());
        if (needQuotation) {
            builder.append(Tokens.DOUBLE_QUOTE);
        }
        builder.append(Tokens.COMPOUND_KEY_VALUE_SEPARATOR);
        if (snbtStyle != SNBTStyle.COMPACT) {
            builder.append(Tokens.SPACE);
        }
        stringify(subTag);
    }

    private void stringifyListTag(ListTag listTag) {
        if (snbtStyle == SNBTStyle.INDENTED) {
            stringifyListTagIfLineBreak(listTag);
        } else {
            stringifyListTagIfNotLineBreak(listTag);
        }
    }

    private void stringifyListTagIfLineBreak(ListTag listTag) {
        builder.append(Tokens.ARRAY_BEGIN);
        if (listTag.size() != 0) {
            depth++;
            boolean isFirst = true;
            for (Tag subTag : listTag) {
                if (!isFirst) {
                    builder.append(Tokens.VALUE_SEPARATOR);
                } else {
                    isFirst = false;
                }
                newLineAndAddTab();
                stringify(subTag);
            }
            depth--;
            newLineAndAddTab();
        }
        builder.append(Tokens.ARRAY_END);
    }

    private void stringifyListTagIfNotLineBreak(ListTag listTag) {
        builder.append(Tokens.ARRAY_BEGIN);
        if (listTag.size() != 0) {
            boolean isFirst = true;
            for (Tag subTag : listTag) {
                if (!isFirst) {
                    builder.append(Tokens.VALUE_SEPARATOR);
                    if (snbtStyle == SNBTStyle.SPACED) {
                        builder.append(Tokens.SPACE);
                    }
                } else {
                    isFirst = false;
                }
                stringify(subTag);
            }
        }
        builder.append(Tokens.ARRAY_END);
    }

    private void stringifyStringTag(StringTag stringTag) {
        stringifyString(stringTag.getValue());
    }

    private void stringifyString(String string) {
        builder.append(Tokens.DOUBLE_QUOTE).append(escape(string)).append(Tokens.DOUBLE_QUOTE);
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
        int i = 0;
        while (i < depth) {
            builder.append(Tokens.TAB);
            i++;
        }
    }

    private void newLineAndAddTab() {
        builder.append(Tokens.NEWLINE);
        addTab();
    }

    //All escape character supported by snbt and `"`
    private static String escape(String input) {
        if (input == null || input.isEmpty()) return input;

        int len = input.length();
        StringBuilder sb = new StringBuilder(len + 8);

        for (int i = 0; i < len; i++) {
            char c = input.charAt(i);
            switch (c) {
                case '\n' -> sb.append("\\n");
                case '\t' -> sb.append("\\t");
                case '\r' -> sb.append("\\r");
                case '\\' -> sb.append("\\\\");
                case '"' -> sb.append("\\\"");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }
}

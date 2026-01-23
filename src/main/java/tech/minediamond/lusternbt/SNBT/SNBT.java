package tech.minediamond.lusternbt.SNBT;

import tech.minediamond.lusternbt.tag.builtin.Tag;

import java.io.IOException;
import java.nio.file.Path;

public class SNBT {
    private SNBT() {
    }

    /**
     * Serialize the specified {@link Tag} into an SNBT string.
     *
     * @param tag         NBT tag that needs to be serialized.
     * @param snbtStyle Whether to enable line wrapping. If {@code true}, output a beautified multi-line string; otherwise, output a single-line compressed string.
     * @return Serialized SNBT text.
     */
    public static String serialize(Tag tag, boolean stringifyRootTagName, SNBTStyle snbtStyle) {
        return new SNBTWriter(tag, stringifyRootTagName, snbtStyle).getSNBTString();
    }

    /**
     * Serializes the specified {@link Tag} and writes it to the specified file path.
     *
     * @param tag         NBT tag that needs to be serialized.
     * @param path        The target file path.
     * @param snbtStyle Whether to enable line wrapping. If {@code true}, output a beautified multi-line string; otherwise, output a single-line compressed string.
     * @throws IOException If an I/O error occurs during file writing.
     */
    public static void writeAsSNBT(Tag tag, boolean stringifyRootTagName, Path path, SNBTStyle snbtStyle) throws IOException {
        new SNBTWriter(tag, stringifyRootTagName, snbtStyle).writeSNBT(path);
    }

    /**
     * Parses an SNBT-formatted string back into a {@link Tag} object.
     *
     * @param SNBTText A string conforming to the SNBT specification.
     * @return The parsed NBT tag.
     * @throws SNBTParseException If SNBT syntax is incorrect and causes parsing to fail
     */
    public static Tag deserialize(String SNBTText) {
        return new SNBTReader(SNBTText).getTag();
    }

    /**
     * Parses an SNBT-formatted string back into a {@link Tag} object from a file.
     *
     * @param path The file path for storing SNBT data.
     * @return The parsed NBT tag.
     * @throws SNBTParseException If SNBT syntax is incorrect and causes parsing to fail
     * @throws IOException        If an I/O error occurs during file writing.
     */
    public static Tag readSNBT(Path path) throws IOException {
        return new SNBTReader(path).getTag();
    }
}

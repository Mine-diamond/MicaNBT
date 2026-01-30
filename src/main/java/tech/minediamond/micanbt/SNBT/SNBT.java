package tech.minediamond.micanbt.SNBT;

import tech.minediamond.micanbt.tag.Tag;

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
    public static String stringify(Tag tag, boolean includeRootName, SNBTStyle snbtStyle) {
        return new SNBTWriter(tag, includeRootName, snbtStyle).getSNBTText();
    }

    /**
     * Serializes the specified {@link Tag} and writes it to the specified file path.
     *
     * @param tag         NBT tag that needs to be serialized.
     * @param path        The target file path.
     * @param snbtStyle Whether to enable line wrapping. If {@code true}, output a beautified multi-line string; otherwise, output a single-line compressed string.
     * @throws IOException If an I/O error occurs during file writing.
     */
    public static void write(Tag tag, boolean includeRootName, Path path, SNBTStyle snbtStyle) throws IOException {
        new SNBTWriter(tag, includeRootName, snbtStyle).write(path);
    }

    /**
     * Parses an SNBT-formatted string back into a {@link Tag} object.
     *
     * @param SNBTText A string conforming to the SNBT specification.
     * @return The parsed NBT tag.
     * @throws SNBTParseException If SNBT syntax is incorrect and causes parsing to fail
     */
    public static Tag parse(String SNBTText) {
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
    public static Tag read(Path path) throws IOException {
        return new SNBTReader(path).getTag();
    }
}

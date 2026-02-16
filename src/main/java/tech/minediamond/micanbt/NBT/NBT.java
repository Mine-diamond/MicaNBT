package tech.minediamond.micanbt.NBT;

import tech.minediamond.micanbt.core.CompoundSelection;
import tech.minediamond.micanbt.tag.CompoundTag;
import tech.minediamond.micanbt.tag.Tag;

import java.io.DataInput;
import java.io.IOException;
import java.nio.file.Path;

public class NBT {

    /**
     * Initiates a builder to read NBT data from a file path.
     *
     * @param path The path to the file.
     * @return An NBTReader Builder.
     */
    public static NBTReader.Builder fromPath(Path path) {
        return NBTReader.builder(path);
    }

    /**
     * Initiates a builder to read NBT data from a {@link DataInput}.
     *
     * @param input The data input source.
     * @return An NBTReader Builder.
     */
    public static NBTReader.Builder fromDataInput(DataInput input) {
        return NBTReader.builder(input);
    }

    /**
     * Initiates a builder to read NBT data from a byte array.
     *
     * @param data The byte array.
     * @return An NBTReader Builder.
     */
    public static NBTReader.Builder fromBytes(byte[] data) {
        return NBTReader.builder(data);
    }

    /**
     * Reads the root NBT tag from a file path with automatic settings.
     *
     * @param path The path to the file.
     * @return The parsed root {@link Tag}.
     * @throws IOException If an I/O error occurs.
     */
    public static Tag read(Path path) throws IOException {
        return NBTReader.builder(path).getTag();
    }

    /**
     * Reads the root NBT tag from a file path with full configuration.
     *
     * @param path              The path to the file.
     * @param compressType      The specific compression type to use.
     * @param littleEndian      Whether to use Little Endian byte order.
     * @param compoundSelection The implementation strategy for CompoundTags.
     * @return The parsed root {@link Tag}.
     * @throws IOException If an I/O error occurs.
     */
    public static Tag read(Path path, NBTCompressType compressType, boolean littleEndian, CompoundSelection compoundSelection) throws IOException {
        return fromPath(path).compressType(compressType).littleEndian(littleEndian).compoundSelection(compoundSelection).getTag();
    }

    /**
     * Reads the root NBT tag from a {@link DataInput}.
     *
     * @param dataInput The data input source.
     * @return The parsed root {@link Tag}.
     * @throws IOException If an I/O error occurs.
     */
    public static Tag read(DataInput dataInput) throws IOException {
        return fromDataInput(dataInput).getTag();
    }

    /**
     * Reads the root NBT tag from a {@link DataInput} with a specific compound selection strategy.
     *
     * @param dataInput         The data input source.
     * @param compoundSelection The implementation strategy for CompoundTags.
     * @return The parsed root {@link Tag}.
     * @throws IOException If an I/O error occurs.
     */
    public static Tag read(DataInput dataInput, CompoundSelection compoundSelection) throws IOException {
        return fromDataInput(dataInput).compoundSelection(compoundSelection).getTag();
    }

    /**
     * Reads the root NBT tag from a byte array with automatic settings.
     *
     * @param data The byte array containing NBT data.
     * @return The parsed root {@link Tag}.
     * @throws IOException If an I/O error occurs.
     */
    public static Tag read(byte[] data) throws IOException {
        return fromBytes(data).getTag();
    }

    /**
     * Reads the root NBT tag from a byte array with full configuration.
     *
     * @param data              The byte array.
     * @param compressType      The specific compression type to use.
     * @param littleEndian      Whether to use Little Endian byte order.
     * @param compoundSelection The implementation strategy for CompoundTags.
     * @return The parsed root {@link Tag}.
     * @throws IOException If an I/O error occurs.
     */
    public static Tag read(byte[] data, NBTCompressType compressType, boolean littleEndian, CompoundSelection compoundSelection) throws IOException {
        return fromBytes(data).compressType(compressType).littleEndian(littleEndian).compoundSelection(compoundSelection).getTag();
    }

    public static void write(CompoundTag tag, Path path) throws IOException {
        new NBTWriter(tag, path, NBTCompressType.UNCOMPRESSED, false);
    }

    public static void write(CompoundTag tag, Path path, NBTCompressType compressType, boolean littleEndian) throws IOException {
        new NBTWriter(tag, path, compressType, littleEndian);
    }
}

package tech.minediamond.micanbt.nbt;

import org.jetbrains.annotations.Contract;
import tech.minediamond.micanbt.tag.CompoundTag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

public class NBT {

    /// Initiates a builder to read NBT data from a file path.
    ///
    /// @param path The path to the file.
    /// @return An NBTReader Builder.
    @Contract("_ -> new")
    public static NBTReader.Builder fromPath(Path path) {
        return NBTReader.builder(path);
    }

    /// Initiates a builder to read NBT data from a [DataInput].
    ///
    /// @param input The data input source.
    /// @return An NBTReader Builder.
    @Contract("_ -> new")
    public static NBTReader.Builder fromDataInput(DataInput input) {
        return NBTReader.builder(input);
    }

    /// Initiates a builder to read NBT data from a byte array.
    ///
    /// @param data The byte array.
    /// @return An NBTReader Builder.
    @Contract("_ -> new")
    public static NBTReader.Builder fromBytes(byte[] data) {
        return NBTReader.builder(data);
    }

    /// Reads the root NBT tag from a file path with automatic settings.
    ///
    /// @param path The path to the file.
    /// @return The parsed root [CompoundTag].
    /// @throws IOException If an I/O error occurs.
    public static CompoundTag read(Path path) throws IOException {
        return NBTReader.builder(path).getTag();
    }

    /// Reads the root NBT tag from a file path with full configuration.
    ///
    /// @param path              The path to the file.
    /// @param compressType      The specific compression type to use.
    /// @param littleEndian      Whether to use Little Endian byte order.
    /// @return The parsed root [CompoundTag].
    /// @throws IOException If an I/O error occurs.
    public static CompoundTag read(Path path, NBTCompressType compressType, boolean littleEndian) throws IOException {
        return fromPath(path).compressType(compressType).littleEndian(littleEndian).getTag();
    }

    /// Reads the root NBT tag from a [DataInput].
    ///
    /// @param dataInput The data input source.
    /// @return The parsed root [CompoundTag].
    /// @throws IOException If an I/O error occurs.
    public static CompoundTag read(DataInput dataInput) throws IOException {
        return fromDataInput(dataInput).getTag();
    }

    /// Reads the root NBT tag from a byte array with automatic settings.
    ///
    /// @param data The byte array containing NBT data.
    /// @return The parsed root [CompoundTag].
    /// @throws IOException If an I/O error occurs.
    public static CompoundTag read(byte[] data) throws IOException {
        return fromBytes(data).getTag();
    }

    /// Reads the root NBT tag from a byte array with full configuration.
    ///
    /// @param data              The byte array.
    /// @param compressType      The specific compression type to use.
    /// @param littleEndian      Whether to use Little Endian byte order.
    /// @return The parsed root [CompoundTag].
    /// @throws IOException If an I/O error occurs.
    public static CompoundTag read(byte[] data, NBTCompressType compressType, boolean littleEndian) throws IOException {
        return fromBytes(data).compressType(compressType).littleEndian(littleEndian).getTag();
    }

    /// Initiates a builder to write NBT data to a file path.
    ///
    /// @param tag  The root `CompoundTag` to write.
    /// @param path The path to the file.
    /// @return An `NBTWriter.Builder` for configuration.
    @Contract("_, _ -> new")
    public static NBTWriter.Builder toPath(CompoundTag tag, Path path) {
        return NBTWriter.builder(tag, path);
    }

    /// Initiates a builder to write NBT data to an `OutputStream`.
    ///
    /// @param tag          The root `CompoundTag` to write.
    /// @param outputStream The destination output stream.
    /// @return An `NBTWriter.Builder` for configuration.
    @Contract("_, _ -> new")
    public static NBTWriter.Builder toStream(CompoundTag tag, OutputStream outputStream) {
        return NBTWriter.builder(tag, outputStream);
    }

    /// Initiates a builder to write NBT data to a `DataOutput`.
    ///
    /// @param tag        The root `CompoundTag` to write.
    /// @param dataOutput The destination data output.
    /// @return An `NBTWriter.Builder` for configuration.
    @Contract("_, _ -> new")
    public static NBTWriter.Builder toDataOutput(CompoundTag tag, DataOutput dataOutput) {
        return NBTWriter.builder(tag, dataOutput);
    }

    /// Writes the root NBT tag to a file path with automatic settings (GZIP compression, Big Endian).
    ///
    /// @param tag  The root `CompoundTag` to write.
    /// @param path The path to the file.
    /// @throws IOException If an I/O error occurs.
    public static void write(CompoundTag tag, Path path) throws IOException {
        toPath(tag, path).write();
    }

    /// Writes the root NBT tag to a file path with full configuration.
    ///
    /// @param tag          The root `CompoundTag` to write.
    /// @param path         The path to the file.
    /// @param compressType The specific compression type to use.
    /// @param littleEndian Whether to use Little Endian byte order.
    /// @throws IOException If an I/O error occurs.
    public static void write(CompoundTag tag, Path path, NBTCompressType compressType, boolean littleEndian) throws IOException {
        toPath(tag, path).compressType(compressType).littleEndian(littleEndian).write();
    }

    /// Writes the root NBT tag to an `OutputStream` with automatic settings (GZIP compression, Big Endian).
    ///
    /// @param tag          The root `CompoundTag` to write.
    /// @param outputStream The destination output stream.
    /// @throws IOException If an I/O error occurs.
    public static void write(CompoundTag tag, OutputStream outputStream) throws IOException {
        toStream(tag, outputStream).write();
    }

    /// Writes the root NBT tag to an `OutputStream` with full configuration.
    ///
    /// @param tag          The root `CompoundTag` to write.
    /// @param outputStream The destination output stream.
    /// @param compressType The specific compression type to use.
    /// @param littleEndian Whether to use Little Endian byte order.
    /// @throws IOException If an I/O error occurs.
    public static void write(CompoundTag tag, OutputStream outputStream, NBTCompressType compressType, boolean littleEndian) throws IOException {
        toStream(tag, outputStream).compressType(compressType).littleEndian(littleEndian).write();
    }

    /// Writes the root NBT tag directly to a `DataOutput`.
    ///
    /// **Note**: This method writes the raw data to the output without additional compression logic.
    ///
    /// @param tag        The root `CompoundTag` to write.
    /// @param dataOutput The destination data output.
    /// @throws IOException If an I/O error occurs.
    public static void write(CompoundTag tag, DataOutput dataOutput) throws IOException {
        toDataOutput(tag, dataOutput).write();
    }

    /// Serializes the root NBT tag into a byte array with automatic settings (GZIP compression, Big Endian).
    ///
    /// @param tag The root `CompoundTag` to serialize.
    /// @return A byte array containing the serialized NBT data.
    /// @throws IOException If an I/O error occurs.
    @Contract(pure = true)
    public static byte[] toBytes(CompoundTag tag) throws IOException {
        return NBTWriter.builder(tag).toByteArray();
    }

    /// Serializes the root NBT tag into a byte array with full configuration.
    ///
    /// @param tag          The root `CompoundTag` to serialize.
    /// @param compressType The specific compression type to use.
    /// @param littleEndian Whether to use Little Endian byte order.
    /// @return A byte array containing the serialized NBT data.
    /// @throws IOException If an I/O error occurs.
    @Contract(pure = true)
    public static byte[] toBytes(CompoundTag tag, NBTCompressType compressType, boolean littleEndian) throws IOException {
        return NBTWriter.builder(tag).compressType(compressType).littleEndian(littleEndian).toByteArray();
    }
}

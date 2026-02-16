package tech.minediamond.micanbt.NBT;

import tech.minediamond.micanbt.core.CompoundSelection;
import tech.minediamond.micanbt.tag.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

/**
 * A reader for Named Binary Tag (NBT) data.
 * <p>
 * This class provides functionality to parse NBT data from various sources including
 * files, byte arrays, and {@link DataInput} streams. It supports automatic inference
 * of compression types (GZIP, ZLIB, or Uncompressed) and configurable byte order.
 */
public class NBTReader {
    private final Path path;
    private final byte[] data;
    private DataInput in;
    private NBTCompressType compressType;
    private final boolean littleEndian;
    private final CompoundSelection compoundSelection;

    private final Tag tag;

    private NBTReader(Builder builder) throws IOException {
        if (builder.path == null && builder.data == null && builder.dataInput == null) {
            throw new IllegalArgumentException("No input source provided to NBTReader");
        }
        this.path = builder.path;
        this.data = builder.data;
        this.compressType = builder.compressType;
        this.littleEndian = builder.littleEndian;
        this.compoundSelection = builder.compoundSelection;
        this.in = builder.dataInput;
        if (in != null) {
            tag = readNamedTag();
        } else {
            tag = inferenceAndRead();
        }
        if (tag == null) {
            throw new NBTParseException("Root tag is end tag");
        }
    }

    /**
     * Creates a new {@link Builder} to read NBT data from a file path.
     *
     * @param path The path to the NBT file.
     * @return A new Builder instance.
     */
    public static Builder builder(Path path) {
        return new Builder(path);
    }

    /**
     * Creates a new {@link Builder} to read NBT data from a {@link DataInput} source.
     *
     * @param dataInput The DataInput stream to read from.
     * @return A new Builder instance.
     */
    public static Builder builder(DataInput dataInput) {
        return new Builder(dataInput);
    }

    /**
     * Creates a new {@link Builder} to read NBT data from a byte array.
     *
     * @param data The byte array containing NBT data.
     * @return A new Builder instance.
     */
    public static Builder builder(byte[] data) {
        return new Builder(data);
    }

    /**
     * Gets the root NBT tag parsed by this reader.
     *
     * @return The parsed root {@link Tag}.
     */
    public Tag getTag() {
        return tag;
    }

    private Tag inferenceAndRead() throws IOException {
        return warpSource();
    }

    private Tag warpSource() throws IOException {
        try (InputStream is = path != null ? new BufferedInputStream(Files.newInputStream(path)) : new ByteArrayInputStream(data)) {
            return inferenceCompressType(is);
        }
    }

    private Tag inferenceCompressType(InputStream is) throws IOException {
        if (compressType == null) {
            is.mark(3);
            byte[] header = new byte[3];
            if (is.read(header) < 3) {
                throw new IOException("File is too small");
            }
            is.reset();

            if (header[0] == 0x1f && header[1] == (byte) 0x8b && header[2] == 0x08) {
                compressType = NBTCompressType.GZIP;
            } else if ((header[0] & 0x0F) == 8 && (header[0] >>> 4) <= 7 && (header[0] * 256 + header[1]) % 31 == 0) {
                compressType = NBTCompressType.ZLIB;
            } else {
                compressType = NBTCompressType.UNCOMPRESSED;
            }
        }

        try (InputStream in = switch (compressType) {
            case UNCOMPRESSED -> is;
            case GZIP -> new GZIPInputStream(is);
            case ZLIB -> new InflaterInputStream(is);
        }) {
            return inferenceLittleEndian(in);
        }
    }

    private Tag inferenceLittleEndian(InputStream in) throws IOException {
        this.in = littleEndian ? new LittleEndianDataInputStream(in) : new DataInputStream(in);
        return readNamedTag();
    }

    private Tag readNamedTag() throws IOException {
        int id = in.readUnsignedByte();
        if (id == 0) {
            return null;
        }
        String name = in.readUTF();
        return createTag(id, name);
    }

    private Tag readAnonymousTag(int id) throws IOException {
        return createTag(id, "");
    }

    private Tag createTag(int id, String name) throws IOException {
        return switch (id) {
            case 1 -> new ByteTag(name, in.readByte());
            case 2 -> new ShortTag(name, in.readShort());
            case 3 -> new IntTag(name, in.readInt());
            case 4 -> new LongTag(name, in.readLong());
            case 5 -> new FloatTag(name, in.readFloat());
            case 6 -> new DoubleTag(name, in.readDouble());
            case 7 -> readByteArrayTag(name);
            case 8 -> new StringTag(name, in.readUTF());
            case 9 -> readListTag(name);
            case 10 -> readCompoundTag(name);
            case 11 -> readIntArrayTag(name);
            case 12 -> readLongArrayTag(name);
            default -> throw new NBTParseException("Could not find tag with ID \"" + id + "\".");
        };
    }

    private CompoundTag readCompoundTag(String name) throws IOException {
        CompoundTag compoundTag = switch (compoundSelection) {
            case COMMON_MAP -> new CommonCompoundTag(name);
            case REORDERABLE_MAP -> new ReorderableCompoundTag(name);
        };
        try {
            Tag tag;
            while ((tag = readNamedTag()) != null) {
                compoundTag.put(tag);
            }
        } catch (EOFException e) {
            throw new NBTParseException("Closing EndTag was not found!");
        }

        return compoundTag;
    }

    private ListTag<Tag> readListTag(String name) throws IOException {
        int typeId = in.readUnsignedByte();
        ListTag<Tag> listTag = new ListTag<>(name, typeId);
        int count = in.readInt();

        if (count > 0 && typeId == 0) {
            throw new NBTParseException("ListTag type is TAG_End but count is > 0");
        }
        for (int index = 0; index < count; index++) {
            listTag.add(readAnonymousTag(typeId));
        }
        return listTag;
    }

    private ByteArrayTag readByteArrayTag(String name) throws IOException {
        byte[] value = new byte[in.readInt()];
        in.readFully(value);
        return new ByteArrayTag(name, value);
    }

    private IntArrayTag readIntArrayTag(String name) throws IOException {
        int[] value = new int[in.readInt()];
        for (int i = 0; i < value.length; i++) {
            value[i] = in.readInt();
        }
        return new IntArrayTag(name, value);
    }

    private LongArrayTag readLongArrayTag(String name) throws IOException {
        long[] value = new long[in.readInt()];
        for (int i = 0; i < value.length; i++) {
            value[i] = in.readLong();
        }
        return new LongArrayTag(name, value);
    }

    /**
     * A fluent Builder for configuring and creating an {@link NBTReader}.
     */
    public static class Builder {
        // one of path, dataInput or data must be provided, and this is ensured through the constructor.
        private Path path;
        private DataInput dataInput;
        private byte[] data;
        // compressed and littleEndian are only required when using path, not needed when using dataInput
        private NBTCompressType compressType; // When not specified, it is automatically inferred; otherwise, the specified value is used.
        private boolean littleEndian = false;
        private CompoundSelection compoundSelection = CompoundSelection.COMMON_MAP;

        private Builder(Path path) {
            this.path = path;
        }

        private Builder(DataInput dataInput) {
            this.dataInput = dataInput;
        }

        private Builder(byte[] data) {
            this.data = data;
        }

        /**
         * Sets the compression type for the NBT data.
         * If not specified, the reader will attempt to infer the type automatically.
         *
         * @param compressType The compression type (GZIP, ZLIB, or UNCOMPRESSED).
         * @return This builder instance.
         */
        public Builder compressType(NBTCompressType compressType) {
            this.compressType = compressType;
            return this;
        }

        /**
         * Sets whether the data should be read in Little Endian byte order.
         * Default is {@code false} (Big Endian).
         *
         * @param littleEndian {@code true} for Little Endian, {@code false} for Big Endian.
         * @return This builder instance.
         */
        public Builder littleEndian(boolean littleEndian) {
            this.littleEndian = littleEndian;
            return this;
        }

        /**
         * Configures the internal map implementation used for {@link CompoundTag}.
         *
         * @param selection The selection strategy (e.g., Common Map or Reorderable Map).
         * @return This builder instance.
         */
        public Builder compoundSelection(CompoundSelection selection) {
            this.compoundSelection = selection;
            return this;
        }

        /**
         * Constructs the {@link NBTReader} and performs the reading operation.
         *
         * @return A fully initialized NBTReader.
         * @throws IOException If an I/O error occurs or the NBT format is invalid.
         */
        public NBTReader build() throws IOException {
            return new NBTReader(this);
        }

        /**
         * A convenience method that builds the reader and returns the root tag immediately.
         *
         * @return The root {@link Tag}.
         * @throws IOException If an I/O error occurs during reading.
         */
        public Tag getTag() throws IOException {
            return new NBTReader(this).getTag();
        }
    }
}
